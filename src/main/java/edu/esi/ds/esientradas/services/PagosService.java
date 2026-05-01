package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired; // Necesario para inyectar el DAO
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.annotation.PostConstruct;
import edu.esi.ds.esientradas.dao.PagoDao;
import edu.esi.ds.esientradas.model.Pago; 


@Service
public class PagosService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Autowired
    private PagoDao pagoDao; // Inyectamos el DAO para poder guardar en BBDD

    @Autowired
    private edu.esi.ds.esientradas.services.ComprasService comprasService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    /** CONSTRUCTOR DE CONFIGURACIÓN
     * Construye la configuración del pago sin ejecutar la llamada externa.
     * Centraliza las reglas de negocio (moneda, cantidad, metadatos).
     */
    private PaymentIntentCreateParams configurarPago(Long centimos, String tokenReserva) {
        return PaymentIntentCreateParams.builder()
                .setAmount(centimos)
                .setCurrency("eur")
                .putMetadata("token_reserva_entrada", tokenReserva) // Vincula el pago al token del PDF
                .build();
    }

    /** ORQUESTADOR
     * Ejecuta la intención de pago con Stripe y persiste el estado en la base de datos.
     */
    public String prepararPago(Long centimos, String tokenReserva) throws StripeException {
        // 1. Obtener la configuración estructurada
        PaymentIntentCreateParams params = configurarPago(centimos, tokenReserva);

        // 2. Comunicación con el servicio externo de Stripe
        PaymentIntent intent = PaymentIntent.create(params);
        
        // 3. Extracción del client_secret para el frontend
        JSONObject jso = new JSONObject(intent.toJson());
        String clientSecret = jso.getString("client_secret");
            
        // 4. Persistencia en MySQL para trazabilidad del estado PENDIENTE
        Pago pago = new Pago(centimos, clientSecret);
        pago.setEstado("PENDIENTE");
        pago.setStripePaymentIntentId(intent.getId());
        pago.setClientSecret(clientSecret);
        pago.setTokenReserva(tokenReserva); // Enlace con el sistema de reservas
        
        this.pagoDao.save(pago); 
        
        return clientSecret;
    }

    /**
     * Comprueba el PaymentIntent en Stripe y, si está completado,
     * completa la compra asociada al token_reserva_entrada guardado en metadata.
     */
    public String confirmarPago(String paymentIntentId, String tokenUsuario) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        if (!"succeeded".equals(intent.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pago no completado: " + intent.getStatus());
        }

        String tokenReserva = intent.getMetadata() != null ? intent.getMetadata().get("token_reserva_entrada") : null;
        if (tokenReserva == null || tokenReserva.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentIntent no contiene token_reserva_entrada en metadata");
        }

        // Delegamos la lógica de marcar la entrada como vendida al servicio de compras
        return this.comprasService.comprar(tokenReserva, tokenUsuario);
    }

    /**
     * Procesa los eventos entrantes de Stripe y finaliza la compra cuando llega payment_intent.succeeded.
     */
    @jakarta.transaction.Transactional
    public String procesarWebhook(String payload, String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Stripe webhook secret no configurado");
        }

        final Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firma del webhook inválida", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Webhook de Stripe inválido", e);
        }

        if (!"payment_intent.succeeded".equals(event.getType())) {
            return "Evento ignorado: " + event.getType();
        }

        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se pudo deserializar el PaymentIntent del webhook"));

        String paymentIntentId = paymentIntent.getId();
        Pago pago = this.pagoDao.findByStripePaymentIntentId(paymentIntentId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un pago registrado para el PaymentIntent: " + paymentIntentId));

        if ("COMPLETADO".equalsIgnoreCase(pago.getEstado())) {
            return "Pago ya procesado anteriormente";
        }

        String resultado = this.comprasService.finalizarCompraDesdeWebhook(pago.getTokenReserva());
        pago.setEstado("COMPLETADO");
        this.pagoDao.save(pago);

        return resultado;
    }
}

package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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

    @Autowired
    private ComprasService comprasService; // Inyectamos el servicio de compras para delegar la lógica de marcar la entrada como vendida

    @Autowired
    private PagoDao pagoDao; // Inyectamos el DAO para poder guardar en BBDD

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
}

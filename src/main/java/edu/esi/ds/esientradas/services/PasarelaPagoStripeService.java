package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Especialista en Stripe. 
 * Esta clase es la única que conoce la librería de Stripe.
 */
@Service
public class PasarelaPagoStripeService implements PasarelaPagoService {

    @Value("${stripe.api.key}") // Inyectamos la clave secreta desde el application.properties
    private String secretKey;

    /**
     * Se ejecuta al arrancar el microservicio.
     * Configura la llave secreta en la librería de Stripe.
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public DtoRespuestaPasarela prepararIntento(Long centimos, String tokenReserva) throws Exception {
        // 1. Configuramos los parámetros específicos de Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(centimos) // Cantidad en céntimos (ej: 1000 para 10€)
                .setCurrency("eur") // Moneda (euros en este caso)
                .putMetadata("token_reserva_entrada", tokenReserva) // Guardamos el token de reserva en Stripe
                .build();

        // 2. Llamada real a los servidores de Stripe
        PaymentIntent intent = PaymentIntent.create(params);

        // 3. Mapeo manual al DTO (El "trasvase" que comentamos antes)
        DtoRespuestaPasarela dtoRespuestaPasarela = new DtoRespuestaPasarela();
        dtoRespuestaPasarela.setIdIntento(intent.getId());
        dtoRespuestaPasarela.setClientSecret(intent.getClientSecret());

        return dtoRespuestaPasarela;
    }

    @Override
    public boolean verificarSiFueExitoso(String idIntento) throws Exception {
        // Recuperamos el estado actual del pago en Stripe
        PaymentIntent intent = PaymentIntent.retrieve(idIntento);
        return "succeeded".equals(intent.getStatus());
    }

    @Override
    public String obtenerTokenReserva(String idIntento) throws Exception {
        // Extraemos la información que guardamos en los metadatos
        PaymentIntent intent = PaymentIntent.retrieve(idIntento);
        return intent.getMetadata().get("token_reserva_entrada");
    }
}
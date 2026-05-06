package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementacion de pasarela para Stripe.
 */
@Service
public class PasarelaPagoStripeService implements PasarelaPagoService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public DtoRespuestaPasarela prepararIntento(Long centimos, String tokenReserva) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(centimos)
                .setCurrency("eur")
                .putMetadata("token_reserva_entrada", tokenReserva)
                .build();
        PaymentIntent intent = PaymentIntent.create(params);
        return new DtoRespuestaPasarela(intent.getId(), intent.getClientSecret());
    }

    @Override
    public boolean verificarSiFueExitoso(String idIntento) throws Exception {
        PaymentIntent intent = PaymentIntent.retrieve(idIntento);
        return "succeeded".equals(intent.getStatus());
    }

    @Override
    public String obtenerTokenReserva(String idIntento) throws Exception {
        PaymentIntent intent = PaymentIntent.retrieve(idIntento);
        return intent.getMetadata().get("token_reserva_entrada");
    }
}

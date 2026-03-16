package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired; // Necesario para inyectar el DAO
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import edu.esi.ds.esientradas.dao.PagoDao;
import edu.esi.ds.esientradas.model.Pago; 


@Service
public class PagosService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Autowired
    private PagoDao pagoDao; // Inyectamos el DAO para poder guardar en BBDD

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String prepararPago(Long centimos) throws StripeException {
        // 1. Crear la intención de pago en Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(centimos)
                .setCurrency("eur")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        
        // 2. Extraer el client_secret usando JSON
        JSONObject jso = new JSONObject(intent.toJson());
        String clientSecret = jso.getString("client_secret");
            
        // 3. GUARDAR EN BBDD (Ahora dentro del flujo)
        Pago pago = new Pago(centimos);
        pago.setEstado("PENDIENTE");
        pago.setStripePaymentIntentId(intent.getId());
        pago.setClientSecret(clientSecret);
        
        this.pagoDao.save(pago); // Persistimos en la base de datos
        
        System.out.println("Pago guardado en BBDD con ID: " + pago.getId());
        
        return clientSecret;
    }
}
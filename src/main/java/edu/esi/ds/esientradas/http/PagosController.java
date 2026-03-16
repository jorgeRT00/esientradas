package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.StripeException;
import edu.esi.ds.esientradas.services.PagosService;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "http://localhost:4200") // Permitir solicitudes desde cualquier origen (útil para desarrollo)
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @PostMapping("/prepararPago") // Endpoint para crear un intento de pago
    public Map<String, String> prepararPago(@RequestBody Map<String, Object> infoPago) { // Recibimos los datos del pago (ej: precio) desde Angular
        Long centimos = ((Number) infoPago.get("precio")).longValue() * 100; // Convertimos el precio a céntimos
        try {
            String result = this.pagosService.prepararPago(centimos); // Creamos el intento de pago con Stripe
            return Map.of("clientSecret", result);
        } catch (StripeException e) {
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }
}
package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.StripeException;
import edu.esi.ds.esientradas.services.PagosService;
import edu.esi.ds.esientradas.services.ReservasService;

import java.util.Map;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "http://localhost:4200") // Permitir solicitudes desde cualquier origen (útil para desarrollo)
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @Autowired
    private ReservasService reservasService; // Inyectamos el servicio que maneja las reservas

    @PostMapping("/prepararPago") // Endpoint para crear un intento de pago
    public Map<String, String> prepararPago(@RequestBody Map<String, String> infoPeticionMap) { // Recibimos los datos de la peticion de la entrada a comprar
        
        // 1. Extraemos el token de reserva de entrada que nos manda el Frontend
        String tokenReservaEntrada = infoPeticionMap.get("tokenReservaEntrada");

        try {
            // 2. Le pedimos al reservas Service que busque las entradas de ese token
            // ... y sume el precio
            Long centimos = this.reservasService.calcularTotalPorToken(tokenReservaEntrada);

            // 3. Pasar los céntimos al servicio de pagos para crear el intento de pago con Stripe
            String result = this.pagosService.prepararPago(centimos); // Creamos el intento de pago con Stripe

            // 4. Devolvemos la "llave" al frontend en formato de diccionario (JSON)
            return Map.of("clientSecret", result);

        } catch (StripeException e) {
            // Si Stripe falla (por ejemplo, clave incorrecta), devolvemos el error
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
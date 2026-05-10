package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.services.PagosService;
import edu.esi.ds.esientradas.services.ReservasService;
import edu.esi.ds.esientradas.services.ComprasService;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "http://localhost:4200") // Permitir solicitudes desde cualquier origen (útil para desarrollo)
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @Autowired
    private ComprasService comprasService; // Delegamos la compra final a este servicio

    @Autowired
    private ReservasService reservasService; // Inyectamos el servicio que maneja las reservas

    @PostMapping("/prepararPago") // Endpoint para crear un intento de pago
    public Map<String, String> prepararPago(@RequestBody Map<String, String> infoPeticionMap) { // Recibimos los datos de la peticion de la entrada a comprar

        // 1. Extraemos el token de reserva de entrada que nos manda el Frontend
        String tokenReservaEntrada = infoPeticionMap.get("tokenReservaEntrada");
        Long centimos = this.reservasService.calcularTotalPorToken(tokenReservaEntrada);

        try {
            String result = this.pagosService.prepararPago(centimos, tokenReservaEntrada);
            return Map.of("clientSecret", result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al preparar el pago");
        }
    }

    @PostMapping("/confirmar") // Endpoint para confirmar el pago desde el frontend o webhook
    public Map<String, String> confirmarPago(@RequestBody Map<String, String> info) {
        String paymentIntentId = info.get("paymentIntentId");
        String tokenUsuario = info.get("tokenUsuario");

        try {
            String tokenReserva = this.pagosService.confirmarPago(paymentIntentId);
            String resultado = this.comprasService.comprar(tokenReserva, tokenUsuario);
            return Map.of("result", resultado);
        } catch (ResponseStatusException e) {
            throw e; // Re-lanzamos la excepción para que Spring maneje el error con el código HTTP adecuado
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al confirmar el pago");
        }
    }
}
package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.dao.PagoDao;
import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;
import edu.esi.ds.esientradas.model.Pago;

@Service
public class PagosService {

    @Autowired
    private PasarelaPagoService pasarela;

    @Autowired
    private PagoDao pagoDao;

    /**
     * Ejecuta la intención de pago en la pasarela y persiste el estado en la base
     * de datos.
     */
    public String prepararPago(Long centimos, String tokenReserva) throws Exception {
        DtoRespuestaPasarela respuestaPago;
        try {
            respuestaPago = pasarela.prepararIntento(centimos, tokenReserva);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo preparar el pago", e);
        }

        Pago pago = new Pago(centimos, tokenReserva);
        pago.setEstado("PENDIENTE");
        pago.setStripePaymentIntentId(respuestaPago.getIdIntento());
        pago.setClientSecret(respuestaPago.getClientSecret());
        pago.setTokenReserva(tokenReserva);

        this.pagoDao.save(pago);

        return respuestaPago.getClientSecret();
    }

    /**
     * Verifica el pago y devuelve el token de reserva asociado.
     */
    public String confirmarPago(String paymentIntentId) throws Exception {
        if (!pasarela.verificarSiFueExitoso(paymentIntentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago no ha sido completado");
        }

        String tokenReserva = pasarela.obtenerTokenReserva(paymentIntentId);
        if (tokenReserva == null || tokenReserva.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error de trazabilidad: reserva no encontrada");
        }

        return tokenReserva;
    }
}

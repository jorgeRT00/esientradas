package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired; // Necesario para inyectar el DAO
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.dao.PagoDao;
import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;
import edu.esi.ds.esientradas.model.Pago; 

@Service
public class PagosService {

    @Autowired
    private ComprasService comprasService; // Inyectamos el servicio de compras para delegar la lógica de marcar la entrada como vendida

    @Autowired
    private PagoDao pagoDao; // Inyectamos el DAO para poder guardar en BBDD

    @Autowired
    private PasarelaPagoService pasarela; // Inyectamos la interfaz de la pasarela de pago para desacoplar la lógica de Stripe

    /** ORQUESTADOR
     * LLama a la pasarela y guarda el rastro en la bbdd (uso del dao) para luego poder confirmar el pago.
      * El controller solo se comunica con este servicio, que es el que orquesta la lógica
     */
    public String prepararPago(Long centimos, String tokenReserva) throws Exception {
        DtoRespuestaPasarela respuestaPago;
        try {
            respuestaPago = pasarela.prepararIntento(centimos, tokenReserva);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo preparar el pago", e);
        }
            
        // 2. Persistencia en MySQL para trazabilidad 
        Pago pago = new Pago(centimos, respuestaPago.getClientSecret());
        pago.setEstado("PENDIENTE");
        pago.setStripePaymentIntentId(respuestaPago.getIdIntento());
        pago.setClientSecret(respuestaPago.getClientSecret());
        pago.setTokenReserva(tokenReserva); 
        
        this.pagoDao.save(pago); 
        
        return respuestaPago.getClientSecret();
    }

    /**
     * CONFIRMACIÓN
     * Verifica que el dinero está en el banco y finaliza la compra.
     */
    public String confirmarPago(String paymentIntentId, String tokenUsuario) throws Exception {
        
        // 1. Verificación genérica: ¿Ha pagado?
        if (!pasarela.verificarSiFueExitoso(paymentIntentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago no ha sido completado");
        }

        // 2. Recuperamos el token de reserva que guardamos en los metadatos
        String tokenReserva = pasarela.obtenerTokenReserva(paymentIntentId);
        
        if (tokenReserva == null || tokenReserva.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de trazabilidad: reserva no encontrada");
        }

        // 3. Delegamos el fin de la compra al servicio correspondiente
        return this.comprasService.comprar(tokenReserva, tokenUsuario);
    }
}
package edu.esi.ds.esientradas.services;

import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;

/**
 * Interfaz que define el comportamiento de una pasarela de pago.
 */
public interface PasarelaPagoService {
    DtoRespuestaPasarela prepararIntento(Long centimos, String tokenReserva) throws Exception;

    boolean verificarSiFueExitoso(String idIntento) throws Exception;

    String obtenerTokenReserva(String idIntento) throws Exception;
}

package edu.esi.ds.esientradas.services;

import edu.esi.ds.esientradas.dto.DtoRespuestaPasarela;
/**
 * Interfaz que define el comportamiento que debe tener 
 * cualquier pasarela de pago en nuestro sistema.
 */
public interface PasarelaPagoService {

    /**
     * Crea un intento de pago en el proveedor externo.
     * @param centimos Cantidad a cobrar.
     * @param tokenReserva Referencia de la reserva de entrada.
     * @return DTO con la información básica para el frontend y el servicio.
     */
    DtoRespuestaPasarela prepararIntento(Long centimos, String tokenReserva) throws Exception;

    /**
     * Verifica si un pago se ha completado con éxito en el proveedor.
     */
    boolean verificarSiFueExitoso(String idIntento) throws Exception;

    /**
     * Recupera el token de reserva asociado a un pago.
     */
    String obtenerTokenReserva(String idIntento) throws Exception;
}
package edu.esi.ds.esientradas.dto;

/**
 * DTO para transportar la respuesta de la pasarela de pagos.
 * Centraliza el ID de la operación y el secreto necesario para el frontend.
 */
public class DtoRespuestaPasarela {
    private String idIntento;
    private String clientSecret;

    // Constructor vacío (necesario para frameworks de serialización)
    public DtoRespuestaPasarela() {}

    // Constructor con parámetros
    public DtoRespuestaPasarela(String idIntento, String clientSecret) {
        this.idIntento = idIntento;
        this.clientSecret = clientSecret;
    }

    // Getters y Setters (Siguiendo tu estilo para que Angular los lea bien)
    public String getIdIntento() {
        return idIntento;
    }

    public void setIdIntento(String idIntento) {
        this.idIntento = idIntento;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
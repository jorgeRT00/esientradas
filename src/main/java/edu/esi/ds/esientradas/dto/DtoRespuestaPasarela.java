package edu.esi.ds.esientradas.dto;

/**
 * DTO para transportar la respuesta de la pasarela de pagos.
 */
public class DtoRespuestaPasarela {
    private String idIntento;
    private String clientSecret;

    public DtoRespuestaPasarela() {
    }

    public DtoRespuestaPasarela(String idIntento, String clientSecret) {
        this.idIntento = idIntento;
        this.clientSecret = clientSecret;
    }

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

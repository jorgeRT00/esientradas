package edu.esi.ds.esientradas.dto;

/**
 * DTO mínimo para Escenario, incluye nombre y tipo.
 * Usado en DtoEspectaculo para que el frontend sepa qué tipo de recinto es.
 */
public class EscenarioDTO {
    private String nombre;
    private String tipo; // TEATRO, CONCIERTO, ESTADIO

    public EscenarioDTO() {}

    public EscenarioDTO(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

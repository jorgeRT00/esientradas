package edu.esi.ds.esientradas.model;

/**
 * Enumeración que define los tipos de recintos disponibles en el sistema.
 * Cada tipo determina cómo se estructuran y visualizan las entradas.
 */
public enum TipoEscenario {

    /** Recinto de teatro: requiere coordenadas exactas (planta, fila, columna) */
    TEATRO("Teatro"),

    /** Recinto de conciertos: utiliza zonas amplias */
    CONCIERTO("Concierto"),

    /** Recinto de estadio: utiliza zonas amplias */
    ESTADIO("Estadio");

    private final String descripcion;

    TipoEscenario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

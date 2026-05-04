package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;

/**
 * Entidad que representa una entrada para un recinto tipo Concierto o Estadio.
 * Hereda de Entrada y añade un identificador de zona.
 * 
 * Esta clase es parte del patrón de herencia JOINED que permite
 * diferentes tipos de entrada según el tipo de recinto.
 */
@Entity
public class DeZona extends Entrada {
    
    /** Identificador o nombre de la zona (ej: "Pista", "Grada Alta", "VIP") */
    private String zona;

    public DeZona() { 
        super();
    }

    public DeZona(Long precio, Espectaculo espectaculo, Estado estado, String zona) {
        this.precio = precio;
        this.espectaculo = espectaculo;
        this.estado = estado;
        this.zona = zona;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    /**
     * Obtiene una representación textual de la zona.
     * 
     * @return String con la zona
     */
    @Override
    public String toString() {
        return String.format("Zona: %s", zona);
    }
}

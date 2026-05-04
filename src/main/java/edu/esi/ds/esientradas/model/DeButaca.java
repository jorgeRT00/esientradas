package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;

/**
 * Entidad que representa una entrada para un recinto tipo Teatro.
 * Hereda de Entrada y añade coordenadas específicas: planta, fila y butaca.
 * 
 * Esta clase sigue el patrón de herencia JOINED para máxima flexibilidad
 * al diferenciar entre tipos de entrada según el recinto.
 */
@Entity
public class DeButaca extends Entrada {
    
    /** Número de planta (ej: 0 para planta baja, 1 para primera planta) */
    private Integer planta;
    
    /** Número de fila dentro de la planta (ej: 1, 2, 3, ...) */
    private Integer fila;
    
    /** Número de butaca dentro de la fila (ej: 1, 2, 3, ...) */
    private Integer butaca;

    // Constructores
    public DeButaca() {
        super();
    }

    public DeButaca(Long precio, Espectaculo espectaculo, Estado estado, 
                    Integer planta, Integer fila, Integer butaca) {
        this.precio = precio;
        this.espectaculo = espectaculo;
        this.estado = estado;
        this.planta = planta;
        this.fila = fila;
        this.butaca = butaca;
    }

    // Getters y Setters
    public Integer getPlanta() {
        return planta;
    }

    public void setPlanta(Integer planta) {
        this.planta = planta;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getButaca() {
        return butaca;
    }

    public void setButaca(Integer butaca) {
        this.butaca = butaca;
    }

    /**
     * Obtiene una representación textual de las coordenadas de la butaca.
     * 
     * @return String con formato "Planta X, Fila Y, Butaca Z"
     */
    @Override
    public String toString() {
        return String.format("Planta %d, Fila %d, Butaca %d", planta, fila, butaca);
    }
}

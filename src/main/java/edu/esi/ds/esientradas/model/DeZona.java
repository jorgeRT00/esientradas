package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Cumplimos el contrato de Entrada: 
     * DeZona sabe cómo empaquetar sus propios datos de zona.
     */
    @Override
    public Map<String, Object> getUbicacionAsMap() {
        Map<String, Object> ubicacion = new HashMap<>();
        
        ubicacion.put("tipo", "ZONA");
        ubicacion.put("zona", this.zona);
        
        // Comprobamos si la zona es válida (no es nula ni está en blanco)
        boolean valida = this.zona != null && !this.zona.isBlank();
        String descripcion = valida 
            ? String.format("Zona: %s", this.zona)
            : "Ubicación de zona pendiente de completar";
            
        ubicacion.put("descripcion", descripcion);
        
        return ubicacion;
    }
}

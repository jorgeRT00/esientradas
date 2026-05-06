package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Precisa extends Entrada {
    private int fila, columna, planta;

    public Precisa() { 
        super();
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getPlanta() {
        return planta;
    }

    public void setPlanta(int planta) {
        this.planta = planta;
    }

    // POLIMORFISMO: cada tipo de entrada sabe cómo formatear su ubicación
    @Override
    public Map<String, Object> getUbicacionAsMap() {
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("tipo", "BUTACA");
        ubicacion.put("planta", planta);
        ubicacion.put("fila", fila);
        ubicacion.put("columna", columna);
        ubicacion.put("descripcion", String.format("Planta %d, Fila %d, Columna %d", planta, fila, columna));
        // Comprobamos si tiene los datos completos
        boolean completas = this.planta != 0 && this.fila != 0 && this.columna != 0;
        
        String descripcion = completas 
            ? String.format("Planta %d, Fila %d, Columna %d", this.planta, this.fila, this.columna)
            : "Ubicación de butaca pendiente de completar";
            
        ubicacion.put("descripcion", descripcion);
        
        return ubicacion;
    }
}

package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;

@Entity /* Creará tabla DeZona en MySQL */
public class DeZona extends Entrada {
    private Integer zona;

    public DeZona() { 
        super();
    }

    public Integer getZona() {
        return zona;
    }

    public void setZona(Integer zona) {
        this.zona = zona;
    }
}

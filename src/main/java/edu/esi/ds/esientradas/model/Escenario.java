package edu.esi.ds.esientradas.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
// jakarta es una clase de javax, pero con un nuevo nombre
@Entity
public class Escenario {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // para que se genere automaticamente el id
    private Long id;
    private String nombre;
    private String descripcion;
    
    @OneToMany(mappedBy = "escenario") // mappedBy indica que la relación es bidireccional y que el lado propietario es Espectaculo
    private List<Espectaculo> espectaculos = new ArrayList<>(); // para evitar nullpointerexception

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @JsonIgnore
    public List<Espectaculo> getEspectaculos() {
        return espectaculos;
    }

    public void setEspectaculos(List<Espectaculo> espectaculos) {
        this.espectaculos = espectaculos;
    }

    
}

package edu.esi.ds.esientradas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Espectaculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String artista;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY) /* Le dice a Hibernate que hay una relación de muchos a uno entre Espectaculo y Escenario. 
                                Muchos espectáculos pueden estar asociados a un mismo escenario. 
                                FetchType.LAZY: los escenarios se cargarán de la base de datos solo cuando se acceda a ellos, no cuando se cargue el espectáculo. */
    @JoinColumn(name = "escenario_id", nullable = false) /* Le dice a Hibernate que la columna que representa la relación con Escenario se llamará "escenario_id" y que no puede ser nula. */
    private Escenario escenario; 

    @OneToMany(mappedBy = "espectaculo", cascade = CascadeType.ALL, orphanRemoval = true) /* Le dice a Hibernate que hay una relación de uno a muchos entre Espectaculo y Entrada. 
                                Un espectáculo puede tener muchas entradas asociadas. 
                                mappedBy indica que la relación es bidireccional y que el lado propietario es Entrada (es decir, la tabla de Entrada tendrá la columna "espectaculo_id" que representa la relación). 
                                cascade = CascadeType.ALL: si se borra un espectáculo, se borrarán todas sus entradas asociadas. 
                                orphanRemoval = true: si se borra una entrada de la lista de entradas de un espectáculo, se borrará esa entrada de la base de datos. */
    private List<Entrada> entradas = new ArrayList<>(); // para evitar nullpointerexception al acceder a la lista de entradas de un espectáculo que no tiene entradas asociadas

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @JsonIgnore
    public Escenario getEscenario() {
        return escenario;
    }

    public void setEscenario(Escenario escenario) {
        this.escenario = escenario;
    }

    @JsonIgnore
    public List<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas;
    }

}

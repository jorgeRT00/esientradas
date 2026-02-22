package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity /* Le dice a Hibernate que esta clase es una tabla en la BD */
@Inheritance(strategy = InheritanceType.JOINED) /* Estrategia de herencia JOINED: cada clase hija tendrá su propia tabla, y la tabla de la clase padre tendrá solo los campos comunes. 
                                Para obtener una entrada concreta, se hará un JOIN entre la tabla de la clase padre y la tabla de la clase hija. */
public abstract class Entrada { /* Clase abstracta: no se pueden crear instancias de esta clase, solo de sus hijas. */
    @Id /* Le dice a Hibernate que el siguiente campo es la clave primaria de la tabla */
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) /* Le dice a Hibernate que el valor del siguiente campo se generará automáticamente por la base de datos, y que es un valor autoincremental */
    protected Long id; /* Id autogenerado autoincremental*/
    private Long precio; // Ojo: en céntimos de euro

    @ManyToOne(fetch = FetchType.LAZY) /* Le dice a Hibernate que hay una relación de muchos a uno entre Entrada y Espectaculo. 
                                Muchas entradas pueden estar asociadas a un mismo espectáculo. 
                                FetchType.LAZY: las entradas se cargarán de la base de datos solo cuando se acceda a ellas, no cuando se cargue el espectáculo. */
    @JoinColumn(name = "espectaculo_id", nullable = false) /* Le dice a Hibernate que la columna que representa la relación con Espectaculo se llamará "espectaculo_id" y que no puede ser nula. */
    protected Espectaculo espectaculo;

    @Enumerated(EnumType.STRING) /* Le dice a Hibernate que el siguiente campo es un enum, y que se guardará en la base de datos como una cadena con el nombre del valor del enum. */
    protected Estado estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore 
    public Espectaculo getEspectaculo() {
        return espectaculo;
    }

    public void setEspectaculo(Espectaculo espectaculo) {
        this.espectaculo = espectaculo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }
}

package edu.esi.ds.esientradas.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Token {
    @Id @Column(length=36) // para que valor se almacene en una columna de la BD con longitud 36 (UUID tiene 36 caracteres)    
    private String valor;
    private Long hora;
    
    @OneToOne(mappedBy = "tokenReserva") /* Relación inversa One-to-One con Entrada. El lado propietario es Entrada con @JoinColumn. */
    private Entrada entrada;

    public Token() {
        this.valor = UUID.randomUUID().toString();
        this.hora = System.currentTimeMillis();
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }
}

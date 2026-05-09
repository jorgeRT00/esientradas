package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "token_valor")
    private Token token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entrada_id")
    private Entrada entrada;

    public Reserva() {
    }

    public Reserva(Token token, Entrada entrada) {
        this.token = token;
        this.entrada = entrada;
    }

    public Long getId() {
        return id;
    }

    public Token getToken() {
        return token;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

}
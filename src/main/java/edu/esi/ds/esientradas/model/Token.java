package edu.esi.ds.esientradas.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Token {
    @Id @Column(length=32)
    private String valor;
    private Long hora;

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
}

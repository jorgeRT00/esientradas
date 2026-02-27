package edu.esi.ds.esientradas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;

@Entity
// el token tendra valor, hora, entradaId y session.
public class Token {
    
    @Id @Column(length = 36)

    private String valor;
    private Long hora;
    private String sessionId;

    @OneToOne // Relación uno a uno con la entidad Entrada
    @JoinColumn(name = "entrada_id", referencedColumnName = "id") // Especifica la columna de unión
    private Entrada entrada; // Relación con la entidad Entrada
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

    public String getSession() {
        return sessionId;
    }

    public void setSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }
}

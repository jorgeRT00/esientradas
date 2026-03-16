package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Pago en la base de datos.
 * Almacena la información de las transacciones de pago.
 */
@Entity
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String stripePaymentIntentId; // ID del PaymentIntent de Stripe
    private Long cantidad; // Cantidad en céntimos de euro
    private String estado; // PENDIENTE, COMPLETADO, CANCELADO, FALLIDO
    private String clientSecret; // Clave Secreta para el cliente de Stripe
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío (requerido por JPA)
    public Pago() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }
    
    // Constructor con parámetros
    public Pago(Long cantidad) {
        this();
        this.cantidad = cantidad;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }
    
    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }
    
    public Long getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

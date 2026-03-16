package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Entidad que representa configuraciones del sistema en formato clave-valor.
 * Útil para almacenar configuraciones como claves API, URLs, etc.
 */
@Entity
public class Configuration {
    
    @Id
    private String nombre; // Clave de configuración (ej: "stripe.secret.key")
    
    private String valor; // Valor de configuración (máx 255 caracteres)
    
    // Constructor vacío (requerido por JPA)
    public Configuration() {
    }
    
    // Constructor con parámetros
    public Configuration(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
}
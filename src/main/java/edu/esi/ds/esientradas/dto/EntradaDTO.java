package edu.esi.ds.esientradas.dto;

import java.util.Map;

/**
 * DTO (Data Transfer Object) para Entrada.
 * 
 * PROPÓSITO: Transportar datos entre backend y frontend sin
 * exponer la estructura interna de JPA, evitar referencias circulares,
 * y permitir formateo personalizado.
 * 
 * Ejemplo de respuesta JSON:
 * {
 *   "id": 1,
 *   "precio": 5000,
 *   "estado": "VENDIDA",
 *   "nombreEspectaculo": "El Rey León",
 *   "ubicacion": {
 *     "tipo": "BUTACA",
 *     "descripcion": "Planta 1, Fila 5, Butaca 12"
 *   }
 * }
 */
public class EntradaDTO {
    
    /** ID de la entrada */
    private Long id;
    
    /** Precio en céntimos (para evitar decimales) */
    private Long precio;
    
    /** Estado: VENDIDA, DISPONIBLE, RESERVADA */
    private String estado;
    
    /** Email del comprador (si la entrada está vendida) */
    private String emailComprador;
    
    /** Nombre del artista/evento */
    private String nombreEspectaculo;
    
    /** Ubicación formateada dinámicamente
     * Contendrá diferente estructura según tipo:
     * - Si BUTACA: { tipo: "BUTACA", planta: 1, fila: 5, butaca: 12, descripcion: "..." }
     * - Si ZONA: { tipo: "ZONA", zona: "Pista", descripcion: "..." }
     */
    private Map<String, Object> ubicacion;

    // Constructores
    public EntradaDTO() {}

    public EntradaDTO(Long id, Long precio, String estado, String emailComprador,
                      String nombreEspectaculo, Map<String, Object> ubicacion) {
        this.id = id;
        this.precio = precio;
        this.estado = estado;
        this.emailComprador = emailComprador;
        this.nombreEspectaculo = nombreEspectaculo;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public String getNombreEspectaculo() {
        return nombreEspectaculo;
    }

    public void setNombreEspectaculo(String nombreEspectaculo) {
        this.nombreEspectaculo = nombreEspectaculo;
    }

    public Map<String, Object> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Map<String, Object> ubicacion) {
        this.ubicacion = ubicacion;
    }
}

package edu.esi.ds.esientradas.dto;

import java.util.Map;

public class EntradaDTO {
    
    private Long id;
    private Long precio;
    private String estado;
    private String emailComprador;
    private String nombreEspectaculo;
    private String tipoEscenario; // <--- NUEVO CAMPO
    private Map<String, Object> ubicacion;

    public EntradaDTO() {}

    // Constructor actualizado
    public EntradaDTO(Long id, Long precio, String estado, String emailComprador,
                      String nombreEspectaculo, String tipoEscenario, Map<String, Object> ubicacion) {
        this.id = id;
        this.precio = precio;
        this.estado = estado;
        this.emailComprador = emailComprador;
        this.nombreEspectaculo = nombreEspectaculo;
        this.tipoEscenario = tipoEscenario;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPrecio() { return precio; }
    public void setPrecio(Long precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEmailComprador() { return emailComprador; }
    public void setEmailComprador(String emailComprador) { this.emailComprador = emailComprador; }

    public String getNombreEspectaculo() { return nombreEspectaculo; }
    public void setNombreEspectaculo(String nombreEspectaculo) { this.nombreEspectaculo = nombreEspectaculo; }

    public String getTipoEscenario() { return tipoEscenario; }
    public void setTipoEscenario(String tipoEscenario) { this.tipoEscenario = tipoEscenario; }

    public Map<String, Object> getUbicacion() { return ubicacion; }
    public void setUbicacion(Map<String, Object> ubicacion) { this.ubicacion = ubicacion; }
}
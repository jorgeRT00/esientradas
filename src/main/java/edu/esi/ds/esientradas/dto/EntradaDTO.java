package edu.esi.ds.esientradas.dto;

import java.util.Map;

/**
 * DTO para Entrada. Transporta datos al frontend sin exponer la estructura JPA,
 * evita referencias circulares y permite formateo de ubicación personalizado.
 */
public class EntradaDTO {

    private Long id;
    private Long precio;
    private String estado;
    private String emailComprador;
    private String nombreEspectaculo;
    private Map<String, Object> ubicacion;
    private String tipoEscenario;

    public EntradaDTO() {}

    public EntradaDTO(Long id, Long precio, String estado, String emailComprador,
                      String nombreEspectaculo, Map<String, Object> ubicacion, String tipoEscenario) {
        this.id = id;
        this.precio = precio;
        this.estado = estado;
        this.emailComprador = emailComprador;
        this.nombreEspectaculo = nombreEspectaculo;
        this.ubicacion = ubicacion;
        this.tipoEscenario = tipoEscenario;

    }

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

    public Map<String, Object> getUbicacion() { return ubicacion; }
    public void setUbicacion(Map<String, Object> ubicacion) { this.ubicacion = ubicacion; }

    public String getTipoEscenario() { return tipoEscenario; }
    public void setTipoEscenario(String tipoEscenario) { this.tipoEscenario = tipoEscenario; }
}

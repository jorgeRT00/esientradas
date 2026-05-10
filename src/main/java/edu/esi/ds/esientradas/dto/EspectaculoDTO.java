package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

public class EspectaculoDTO {

    private String artista;
    private LocalDateTime fecha;
    private EscenarioDTO escenario; // Ahora es un objeto con nombre y tipo
    private Long id;
    private LocalDateTime fechaAperturaTaquilla;

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setEscenario(EscenarioDTO escenario) {
        this.escenario = escenario;
    }

    public String getArtista() {
        return artista;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EscenarioDTO getEscenario() {
        return escenario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaAperturaTaquilla() {
        return fechaAperturaTaquilla;
    }

    public void setFechaAperturaTaquilla(LocalDateTime fechaAperturaTaquilla) {
        this.fechaAperturaTaquilla = fechaAperturaTaquilla;
    }
}

package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

public class EspectaculoDTO {
    private String artista;
    private LocalDateTime fecha;
    private EscenarioDTO escenario;
    private Long id;
    private LocalDateTime fechaAperturaTaquilla;

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public EscenarioDTO getEscenario() { return escenario; }
    public void setEscenario(EscenarioDTO escenario) { this.escenario = escenario; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaAperturaTaquilla() { return fechaAperturaTaquilla; }
    public void setFechaAperturaTaquilla(LocalDateTime v) { this.fechaAperturaTaquilla = v; }
}

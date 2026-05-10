package edu.esi.ds.esientradas.dto;

public class EstadisticasEspectaculoDTO {
    private Integer totales;
    private Integer reservadas;
    private Integer libres;
    private Integer vendidas;

    // Añade este constructor para que la Query funcione
    public EstadisticasEspectaculoDTO(Long totales, Long libres, Long vendidas, Long reservadas) {
        this.totales = totales != null ? totales.intValue() : 0;
        this.libres = libres != null ? libres.intValue() : 0;
        this.vendidas = vendidas != null ? vendidas.intValue() : 0;
        this.reservadas = reservadas != null ? reservadas.intValue() : 0;
    }
    
    // Getters y Setters (Vital para que Angular los lea)
    public Integer getTotales() {
        return totales;
    }

    public void setTotales(Integer totales) {
        this.totales = totales;
    }

    public Integer getLibres() {
        return libres;
    }

    public void setLibres(Integer libres) {
        this.libres = libres;
    }

    public Integer getVendidas() {
        return vendidas;
    }

    public void setVendidas(Integer vendidas) {
        this.vendidas = vendidas;
    }

    public Integer getReservadas() {
        return reservadas;
    }

    public void setReservadas(Integer reservadas) {
        this.reservadas = reservadas;
    }
}
package edu.esi.ds.esientradas.dto;

public class DtoEntrada {
    private Long id;
    private Long precio;
    private String espectaculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = (long) precio;
    }

    public String getEspectaculo() {
        return espectaculo;
    }

    public void setEspectaculo(String espectaculo) {
        this.espectaculo = espectaculo;
    }
}

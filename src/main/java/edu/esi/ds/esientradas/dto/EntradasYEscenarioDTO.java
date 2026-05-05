package edu.esi.ds.esientradas.dto;

import java.util.List;

/**
 * DTO que agrupa las entradas disponibles con el tipo de escenario.
 * Permite al frontend saber si debe mostrar interfaz de ZONAS o BUTACAS.
 */
public class EntradasYEscenarioDTO {
    private String tipoEscenario; // TEATRO, CONCIERTO, ESTADIO
    private List<EntradaDTO> entradas;

    public EntradasYEscenarioDTO(String tipoEscenario, List<EntradaDTO> entradas) {
        this.tipoEscenario = tipoEscenario;
        this.entradas = entradas;
    }

    public String getTipoEscenario() {
        return tipoEscenario;
    }

    public void setTipoEscenario(String tipoEscenario) {
        this.tipoEscenario = tipoEscenario;
    }

    public List<EntradaDTO> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaDTO> entradas) {
        this.entradas = entradas;
    }
}

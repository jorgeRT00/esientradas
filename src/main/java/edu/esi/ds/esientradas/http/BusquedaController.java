package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.dto.DtoEntradas;
import edu.esi.ds.esientradas.dto.DtoEspectaculo;
import edu.esi.ds.esientradas.dto.EscenarioDTO;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dto.EntradasYEscenarioDTO;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.services.BusquedaService;
import java.util.List;

@RestController
@RequestMapping("/busqueda")
@CrossOrigin(origins = "http://localhost:4200")
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/getEntradas")
    public List<EntradaDTO> getEntradas(@RequestParam Long espectaculoId) {
        return this.service.getEntradasDTO(espectaculoId);
    }

    @GetMapping("/getEntradasDisponibles")
    public List<EntradaDTO> getEntradasDisponibles(@RequestParam Long espectaculoId) {
        return this.service.getEntradasDisponiblesDTO(espectaculoId);
    }

    /**
     * Obtiene las entradas disponibles junto con el tipo de escenario.
     * El frontend usa esto para mostrar interfaz de ZONAS o BUTACAS.
     */
    @GetMapping("/getEntradasConEscenario")
    public EntradasYEscenarioDTO getEntradasConEscenario(@RequestParam Long espectaculoId) {
        return this.service.getEntradasDisponiblesConEscenario(espectaculoId);
    }

    @GetMapping("/getEspectaculos/{escenarioId}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long escenarioId) {
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(escenarioId));
    }

    @GetMapping(value = "/getEspectaculos", params = "escenarioId")
    public List<DtoEspectaculo> getEspectaculosPorEscenario(@RequestParam Long escenarioId) {
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(escenarioId));
    }

    @GetMapping(value = "/getEspectaculos", params = "artista")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(artista));
    }

    /**
     * Mapea espectáculos con protección uniforme contra null.
     */
    private List<DtoEspectaculo> mapearEspectaculosConSeguridad(List<Espectaculo> espectaculos) {
        return espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setFechaAperturaTaquilla(e.getFechaAperturaTaquilla());

            Escenario esc = e.getEscenario();
            String nombreEscenario = "Desconocido";
            String tipoEscenario = "DESCONOCIDO";
            if (esc != null) {
                nombreEscenario = esc.getNombre() != null ? esc.getNombre() : "Desconocido";
                if (esc.getTipo() != null) {
                    tipoEscenario = esc.getTipo().name();
                }
            }
            dto.setEscenario(new EscenarioDTO(nombreEscenario, tipoEscenario));
            return dto;
        }).toList();
    }

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        return this.service.getEscenarios();
    }

    @GetMapping("/saludar/{nombre}")
    public String saludar(@PathVariable String nombre, @RequestParam String apellido) {
        return "Hola, " + nombre + " " + apellido + ", bienvenido a Esientradas!";
    }

    @GetMapping("/getNumeroEntradas/{espectaculoId}")
    public Integer getNumeroEntradas(@PathVariable Long espectaculoId) {
        return this.service.getNumeroEntradas(espectaculoId);
    }

    @GetMapping("/getEntradasLibres/{espectaculoId}")
    public Integer getEntradasLibres(@PathVariable Long espectaculoId) {
        return this.service.getEntradasLibres(espectaculoId);
    }

    @GetMapping("/getNumeroEntradasDto/{espectaculoId}")
    public DtoEntradas getNumeroEntradasDto(@PathVariable Long espectaculoId) {
        return this.service.getNumeroEntradasDto(espectaculoId);
    }
}

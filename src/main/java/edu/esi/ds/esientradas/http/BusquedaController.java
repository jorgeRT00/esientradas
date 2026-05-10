package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dto.EspectaculoDTO;
import edu.esi.ds.esientradas.dto.EstadisticasEspectaculoDTO;
import edu.esi.ds.esientradas.model.Escenario;
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

    @GetMapping("/getEspectaculos/{escenarioId}")
    public List<EspectaculoDTO> getEspectaculos(@PathVariable Long escenarioId) {
        return this.service.getEspectaculos(escenarioId);
    }

    @GetMapping(value = "/getEspectaculos", params = "escenarioId")
    public List<EspectaculoDTO> getEspectaculosPorEscenario(@RequestParam Long escenarioId) {
        return this.service.getEspectaculos(escenarioId);
    }

    @GetMapping(value = "/getEspectaculos", params = "artista")
    public List<EspectaculoDTO> getEspectaculosPorArtista(@RequestParam String artista) {
        return this.service.getEspectaculos(artista);
    }

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        return this.service.getEscenarios();
    }

    @GetMapping("/getNumeroEntradasDto/{espectaculoId}")
    public EstadisticasEspectaculoDTO getNumeroEntradasDto(@PathVariable Long espectaculoId) {
        return this.service.getNumeroEntradasDto(espectaculoId);
    }
}
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
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.services.BusquedaService;
import java.util.List;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.dto.EntradasYEscenarioDTO;

@RestController
@RequestMapping("/busqueda")
@CrossOrigin(origins = "http://localhost:4200") // Permitir solicitudes desde cualquier origen (útil para desarrollo)
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/getEntradas")
    public List<EntradaDTO> getEntradas(@RequestParam Long espectaculoId) {
        // Devuelve DTO formateado con ubicación, evitando referencias circulares
        return this.service.getEntradasDTO(espectaculoId);
    }

    @GetMapping("/getEntradasDisponibles")
    public List<EntradaDTO> getEntradasDisponibles(@RequestParam Long espectaculoId) {
        return this.service.getEntradasDisponiblesDTO(espectaculoId);
    }

    /**
     * Obtiene las entradas disponibles junto con el tipo de escenario.
     * El frontend usa esto para mostrar INTERFAZ A (ZONAS) o INTERFAZ B (BUTACAS).
     */
    @GetMapping("/getEntradasConEscenario")
    public EntradasYEscenarioDTO getEntradasConEscenario(@RequestParam Long espectaculoId) {
        return this.service.getEntradasDisponiblesConEscenario(espectaculoId);
    }

    /**
     * Obtiene espectáculos por escenario con protección contra null.
     * Cubre tanto @PathVariable como @RequestParam escenarioId.
     */
    @GetMapping("/getEspectaculos/{escenarioId}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long escenarioId) {
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(escenarioId));
    }

    @GetMapping(value = "/getEspectaculos", params = "escenarioId")
    public List<DtoEspectaculo> getEspectaculosPorEscenario(@RequestParam Long escenarioId) {
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(escenarioId));
    }

    /**
     * Método privado para mapear espectáculos con protección uniforme contra null.
     * Esto elimina duplicación y garantiza consistencia.
     */
    private List<DtoEspectaculo> mapearEspectaculosConSeguridad(List<Espectaculo> espectaculos) {
        return espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setFechaAperturaTaquilla(e.getFechaAperturaTaquilla());

            // --- PROTECCIÓN UNIFORME CONTRA NULL ---
            Escenario esc = e.getEscenario();
            String nombreEscenario = "Desconocido";
            String tipoEscenario = "DESCONOCIDO"; // Valor por defecto coherente con TipoEscenario

            if (esc != null) {
                nombreEscenario = esc.getNombre() != null ? esc.getNombre() : "Desconocido";
                if (esc.getTipo() != null) {
                    tipoEscenario = esc.getTipo().name(); // TEATRO, CONCIERTO, ESTADIO
                }
            }

            dto.setEscenario(new EscenarioDTO(nombreEscenario, tipoEscenario));
            return dto;
        }).toList();
    }

    @GetMapping(value = "/getEspectaculos", params = "artista")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {
        // Usa el mismo método privado con protección uniforme
        return this.mapearEspectaculosConSeguridad(this.service.getEspectaculos(artista));
    }

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        // aqui se haria la logica para obtener los escenarios de la base de datos
        return this.service.getEscenarios(); // se llama al servicio para obtener los escenarios
    }

    @GetMapping("/saludar/{nombre}")
    public String saludar(@PathVariable String nombre, @RequestParam String apellido) {
        return "Hola, " + nombre + " " + apellido + ", bienvenido a Esientradas!"; // http://localhost:8080/busqueda/saludar?nombre=Jorge&apellido=Rodriguez
    }

    @GetMapping("/getNumeroEntradas/{espectaculoId}")
    public Integer getNumeroEntradas(@PathVariable Long espectaculoId) {
        return this.service.getNumeroEntradas(espectaculoId); // se llama al servicio para obtener el numero de entradas
    }

    @GetMapping("/getEntradasLibres/{espectaculoId}")
    public Integer getEntradasLibres(@PathVariable Long espectaculoId) {
        return this.service.getEntradasLibres(espectaculoId); // se llama al servicio para obtener el numero de entradas
                                                              // libres
    }

    @GetMapping("/getNumeroEntradasDto/{espectaculoId}")
    public DtoEntradas getNumeroEntradasDto(@PathVariable Long espectaculoId) {
        return this.service.getNumeroEntradasDto(espectaculoId); // se llama al servicio para obtener el numero de
                                                                 // entradas a partir del dto
    }
}

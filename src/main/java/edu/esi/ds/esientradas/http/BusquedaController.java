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
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.services.BusquedaService;
import java.util.List;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Entrada;

@RestController
@RequestMapping("/busqueda")
@CrossOrigin(origins = "http://localhost:4200") // Permitir solicitudes desde cualquier origen (útil para desarrollo)
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/getEntradas")
    public List<Entrada> getEntradas(@RequestParam Long espectaculoId) {
        // aqui se haria la logica para obtener las entradas de la base de datos
        return this.service.getEntradas(espectaculoId); // se llama al servicio para obtener las entradas

    }

    @GetMapping("/getEspectaculos/{escenarioId}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long escenarioId) {

        List<Espectaculo> espectaculos = this.service.getEspectaculos(escenarioId); // se llama al servicio para obtener
                                                                                    // los espectaculos

        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
        return dtos;
    }

    @GetMapping("/getEspectaculos")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {

        List<Espectaculo> espectaculos = this.service.getEspectaculos(artista); // se llama al servicio para obtener los
                                                                                // espectaculos

        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
        return dtos;
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

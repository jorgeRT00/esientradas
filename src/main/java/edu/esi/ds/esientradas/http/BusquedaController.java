package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.websocket.server.PathParam; 
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.services.BusquedaService;
import java.util.List;

@RestController
@RequestMapping("/busqueda")
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        // aqui se haria la logica para obtener los escenarios de la base de datos
        return this.service.getEscenarios(); // se llama al servicio para obtener los escenarios
    }

    @GetMapping("/saludar/{nombre}")
    public String saludar(@PathVariable String nombre, @RequestParam String apellido) {
        return "Hola, " + nombre + " " + apellido + ", bienvenido a Esientradas!"; // http://localhost:8080/busqueda/saludar?nombre=Jorge&apellido=Rodriguez
    }

}

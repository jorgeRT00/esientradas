package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.model.Escenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import edu.esi.ds.esientradas.services.EscenariosService;

@RestController
@RequestMapping("/escenarios")
public class EscenarioController {

    @Autowired
    private EscenariosService escenariosService;

    @RequestMapping("/insertar")
    public void insertarEscenario(@RequestBody Escenario escenario) {
        if (escenario.getNombre() == null || escenario.getNombre().isEmpty() || escenario.getDescripcion() == null
                || escenario.getDescripcion().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre y la descripción no pueden ser nulos");
        }
        this.escenariosService.insertarEscenario(escenario);

        System.out.println(escenario.getNombre());
        System.err.println(escenario.getDescripcion());
    }

}

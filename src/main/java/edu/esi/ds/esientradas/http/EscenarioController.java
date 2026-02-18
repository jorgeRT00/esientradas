package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.esi.ds.esientradas.model.Escenario;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/escenarios")
public class EscenarioController {

    @RequestMapping("/insertar")
    public void insertarEscenario(@RequestBody Escenario escenario) {
        System.out.println(escenario.getNombre());
        System.err.println(escenario.getDescripcion());
    }

}

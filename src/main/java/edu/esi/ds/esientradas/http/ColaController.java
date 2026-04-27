package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import edu.esi.ds.esientradas.services.ColaService;

@RestController
@RequestMapping("/cola")
@CrossOrigin(origins = "http://localhost:4200") // Cambia esto por el origen de tu frontend
public class ColaController {

    @Autowired
    private ColaService colaService;

    @PostMapping("/unirse")
    public String unirse(@RequestParam Long espectaculoId, @RequestParam String emailUsuario) {
        return colaService.unirse(espectaculoId, emailUsuario);
    }

    @GetMapping("/posicion")
    public int posicion(@RequestParam Long espectaculoId, @RequestParam String emailUsuario) {
        return colaService.consultarPosicion(espectaculoId, emailUsuario);
    }

    @GetMapping("/turno")
    public boolean tieneTurno(@RequestParam Long espectaculoId, @RequestParam String emailUsuario) {
        return colaService.tieneTurno(espectaculoId, emailUsuario);
    }

    @DeleteMapping("/salir")
    public void salir(@RequestParam Long espectaculoId, @RequestParam String emailUsuario) {
        colaService.salirDeCola(espectaculoId, emailUsuario);
    }

}

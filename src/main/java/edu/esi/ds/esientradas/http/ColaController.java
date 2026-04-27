package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.services.ColaService;
import edu.esi.ds.esientradas.services.UsuariosService;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/cola")
@CrossOrigin(origins = "http://localhost:4200") // Cambia esto por el origen de tu frontend
public class ColaController {

    @Autowired
    private ColaService colaService;

    @Autowired
    private UsuariosService usuariosService;

    private String validarToken(String tokenUsuario) {
        String email = usuariosService.checkToken(tokenUsuario);
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no valido.");
        }
        return email;
    }

    @PostMapping("/unirse")
    public String unirse(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        return colaService.unirse(espectaculoId, validarToken(tokenUsuario));
    }

    @GetMapping("/posicion")
    public int posicion(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        return colaService.consultarPosicion(espectaculoId, validarToken(tokenUsuario));
    }

    @GetMapping("/turno")
    public boolean tieneTurno(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        return colaService.tieneTurno(espectaculoId, validarToken(tokenUsuario));
    }

    @DeleteMapping("/salir")
    public void salir(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        colaService.salirDeCola(espectaculoId, validarToken(tokenUsuario));
    }

}

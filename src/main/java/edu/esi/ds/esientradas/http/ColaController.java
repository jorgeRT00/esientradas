package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.services.ColaService;
import edu.esi.ds.esientradas.services.UsuariosService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/cola")
public class ColaController {

    @Autowired
    private ColaService colaService;

    @Autowired
    private UsuariosService usuariosService;

    @PostMapping("/unirse")
    public String unirse(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        String emailUsuario = usuariosService.checkToken(tokenUsuario);
        if(emailUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no valido.");
        }
        return colaService.unirse(espectaculoId, emailUsuario);
    }

    @GetMapping("/posicion")
    public int posicion(@RequestParam Long espectaculoId, @RequestParam String tokenUsuario) {
        String emailUsuario = usuariosService.checkToken(tokenUsuario);
        if(emailUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no valido.");
        }
        return colaService.consultarPosicion(espectaculoId, emailUsuario);
    }

}

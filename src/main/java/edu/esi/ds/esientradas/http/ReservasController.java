package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.beans.factory.annotation.Autowired;
import edu.esi.ds.esientradas.services.ReservasService;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // se permite el acceso desde el frontend en localhost:4200
@RequestMapping("/reservas")
public class ReservasController {

    @Autowired
    private ReservasService reservasService;
    @Autowired
    private edu.esi.ds.esientradas.services.ComprasService comprasService;

    @PutMapping ("/reservar")
    public String reservar(HttpSession session, @RequestParam Long entradaId) {
        return this.reservasService.reservar(entradaId, session.getId()); // se llama al servicio para reservar la entrada y se devuelve el precio total de las entradas reservadas
    }

    @GetMapping("/comprar")
    public String comprar (@RequestParam String tokenEntrada, @RequestParam String tokenUsuario) {
        return this.comprasService.comprar(tokenEntrada, tokenUsuario); // delega la compra al servicio de compras
    }
}

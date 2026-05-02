package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

import edu.esi.ds.esientradas.services.ComprasService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/compras")
public class ComprasController {

    @Autowired
    private ComprasService comprasService;

    @PostMapping("/comprar")
    public String comprar(@RequestParam String tokenEntrada, @RequestParam String tokenUsuario) {
        return this.comprasService.comprar(tokenEntrada, tokenUsuario);
    }

    @GetMapping("/misEntradas")
    public List<Map<String, Object>> misEntradas(@RequestParam String emailUsuario) {
        return this.comprasService.misEntradas(emailUsuario);
    }
}

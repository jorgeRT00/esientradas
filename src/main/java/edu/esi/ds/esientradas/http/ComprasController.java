package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/ticket/pdf/{entradaId}")
    public ResponseEntity<byte[]> descargarTicketPdf(@PathVariable String entradaId) {
        byte[] pdfContents = this.comprasService.generarTicketPdf(entradaId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "ticket_" + entradaId + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
    }

    @GetMapping("/ticket/zip")
    public ResponseEntity<byte[]> descargarTicketsZip(@RequestParam String emailUsuario) {
        byte[] zipContents = this.comprasService.generarZipMisEntradas(emailUsuario);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDispositionFormData("attachment", "mis_entradas.zip");

        return new ResponseEntity<>(zipContents, headers, HttpStatus.OK);
    }
}

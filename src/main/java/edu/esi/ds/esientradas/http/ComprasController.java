package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders; // ✓ CORREGIDO: De Spring, no de Stripe
import org.springframework.http.MediaType;   // ✓ AGREGADO
import org.springframework.http.HttpStatus;  // ✓ AGREGADO
import org.springframework.web.bind.annotation.*; // ✓ Simplifica los imports de las anotaciones

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
        // Obtenemos el contenido del PDF desde el servicio
        byte[] pdfContents = this.comprasService.generarTicketPdf(entradaId);
        
        // Configuramos las cabeceras de respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        
        // Esto indica al navegador que debe descargar el archivo en lugar de solo mostrarlo
        String filename = "ticket_" + entradaId + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        
        // ✓ CORREGIDO: Usamos pdfContents (el nombre que definiste arriba)
        return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
    }   

    @GetMapping("/ticket/zip")
    public ResponseEntity<byte[]> descargarTicketsZip(@RequestParam List<String> ids) {
        byte[] zipContents = this.comprasService.generarTicketsZip(ids);
        
        HttpHeaders headers = new HttpHeaders();
        // Indicamos que el contenido es un archivo ZIP
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDispositionFormData("attachment", "mis_entradas.zip");
        
        return new ResponseEntity<>(zipContents, headers, HttpStatus.OK);
}
}
package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.Document;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Estado;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.*;

@Service
public class ComprasService {

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private UsuariosService usuariosService;


    @Transactional
    public String comprar(String tokenEntrada, String tokenUsuario) {
        String emailUsuario = this.usuariosService.checkToken(tokenUsuario);
        if (emailUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no valido.");
        }

        Token token = this.tokenDao.findById(tokenEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token de entrada no encontrado."));

        Entrada entrada = token.getEntrada();
        if (entrada.getEstado() == Estado.VENDIDA) {
            return "Entrada ya vendida anteriormente para el usuario: " + emailUsuario;
        }

        entrada.setEstado(Estado.VENDIDA);
        entrada.setEmailComprador(emailUsuario);
        this.entradaDao.save(entrada);

        // Delegamos la generación del PDF a un servicio específico para mantener el código limpio
        byte[] pdfBytes = pdfService.crearPdfEntrada(entrada);

        try {
            emailService.sendEmail(
                emailUsuario, "Compra de entrada exitosa",
                "Has comprado la entrada con ID: " + entrada.getId(),
                pdfBytes,
                "entrada_" + entrada.getId() + ".pdf"
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo enviar el email: " + e.getMessage(), e);
        }

        this.tokenDao.deleteByValorNativo(tokenEntrada);

        return "Compra realizada con exito para el usuario: " + emailUsuario;
    }

    public List<Map<String, Object>> misEntradas(String emailUsuario) {
        List<Entrada> entradas = this.entradaDao.findByEmailComprador(emailUsuario);
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Entrada e : entradas) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("artista", e.getEspectaculo().getArtista());
            map.put("fecha", e.getEspectaculo().getFecha().toString());
            map.put("precio", e.getPrecio() / 100.0);
            resultado.add(map);
        }
        return resultado;
    }

    /**
     * Ahora este método es un "coordinador": pide PDFs y se los pasa al ZipService.
     */
    public byte[] generarTicketsZip(List<String> entradaIds) {
        Map<String, byte[]> archivosParaZip = new HashMap<>();
        
        for (String id : entradaIds) {
            this.entradaDao.findById(Long.parseLong(id)).ifPresent(entrada -> {
                // Pedimos el PDF al especialista
                byte[] pdf = pdfService.crearPdfEntrada(entrada);
                archivosParaZip.put("ticket_" + id + ".pdf", pdf);
            });
        }

        try {
            // Pasamos el mapa de archivos al ZipService
            return zipService.generarZip(archivosParaZip);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al empaquetar el ZIP", e);
        }
    }

    public byte[] generarTicketPdf(String id) {
        // 1. Buscamos la entrada en la BD (convertimos el String a Long)
        Entrada entrada = this.entradaDao.findById(Long.parseLong(id))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));
            
        // 2. Le pasamos el objeto completo al especialista en PDFs
        return pdfService.crearPdfEntrada(entrada);
    }
}
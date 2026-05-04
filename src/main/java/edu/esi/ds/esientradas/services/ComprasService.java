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
    private UsuariosService usuariosService;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    @Autowired
    private EmailService emailService;

    /**
     * MÉTODO NUEVO: Genera el PDF para el Controller de descarga
     */
    public byte[] generarTicketPdf(String entradaId) {
        // Buscamos la entrada (asumiendo que el ID es Long en tu base de datos)
        Entrada entrada = this.entradaDao.findById(Long.parseLong(entradaId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada."));

        return crearPdfEntrada(entrada);
    }

    /**
     * MÉTODO PRIVADO: Centraliza la creación del PDF con iText 7
     * Así lo usamos tanto para el email como para la descarga directa.
     */
    private byte[] crearPdfEntrada(Entrada entrada) {
        try (ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream()) {
            Espectaculo espectaculo = entrada.getEspectaculo();
            
            PdfWriter writer = new PdfWriter(pdfBytes);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("ENTRADA ESIentradas").setBold().setFontSize(18));
            document.add(new Paragraph("Artista: " + espectaculo.getArtista()));
            document.add(new Paragraph("Fecha: " + espectaculo.getFecha().toString()));
            document.add(new Paragraph("ID Entrada: " + entrada.getId()));
            document.add(new Paragraph("Precio: " + (entrada.getPrecio() / 100.0) + " euros"));
            
            document.close();
            return pdfBytes.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF.");
        }
    }

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

        // Generamos el PDF usando el nuevo método centralizado
        byte[] pdfBytesArray = crearPdfEntrada(entrada);

        try {
            emailService.sendEmail(
                emailUsuario,
                "Compra de entrada exitosa",
                "Has comprado la entrada con ID: " + entrada.getId(),
                pdfBytesArray,
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

    public byte[] generarTicketsZip(List<String> entradaIds) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipOutputStream zos = new ZipOutputStream(baos)) {
        
        for (String id : entradaIds) {
            // Buscamos la entrada en la base de datos
            Entrada entrada = this.entradaDao.findById(Long.parseLong(id)).orElse(null);
            
            if (entrada != null) {
                // Reutilizamos tu lógica de creación de PDF
                byte[] pdfBytes = crearPdfEntrada(entrada);
                
                // Creamos un "fichero" dentro del ZIP para este ticket
                ZipEntry entry = new ZipEntry("ticket_" + id + ".pdf");
                zos.putNextEntry(entry);
                zos.write(pdfBytes);
                zos.closeEntry();
            }
        }
        
        zos.finish();
        return baos.toByteArray();
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al empaquetar el ZIP");
    }
}
}
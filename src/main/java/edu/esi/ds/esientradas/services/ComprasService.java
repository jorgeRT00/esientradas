package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Estado;
import java.io.ByteArrayOutputStream;
import java.util.*;
import com.itextpdf.layout.Document;

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

        try {
            Espectaculo espectaculo = entrada.getEspectaculo();
            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfBytes);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.add(new Paragraph("ENTRADA ESIentradas"));
            document.add(new Paragraph("Artista: " + espectaculo.getArtista()));
            document.add(new Paragraph("Fecha: " + espectaculo.getFecha().toString()));
            document.add(new Paragraph("ID Entrada: " + entrada.getId()));
            document.add(new Paragraph("Precio: " + (entrada.getPrecio() / 100.0) + " euros"));
            document.close();
            byte[] pdfBytesArray = pdfBytes.toByteArray();

            emailService.sendEmail(
                emailUsuario,
                "Compra de entrada exitosa",
                "Has comprado la entrada con ID: " + entrada.getId(),
                pdfBytesArray,
                "entrada_" + entrada.getId() + ".pdf"
            );
        } catch (Exception e) {
            System.err.println("Error al enviar el email: " + e.getMessage());
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
}

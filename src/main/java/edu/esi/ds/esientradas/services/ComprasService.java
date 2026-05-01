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

    /**
     * Completa la compra asociada a un token de reserva.
     * Idempotente y transaccional.
     */
    @Transactional
    public String comprar(String tokenEntrada, String tokenUsuario) {
        String emailUsuario = this.usuariosService.checkToken(tokenUsuario);
        if (emailUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no válido.");
        }

        Token token = this.tokenDao.findById(tokenEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token de entrada no encontrado."));

        Entrada entrada = token.getEntrada();

        // Idempotencia: si ya está vendida, devolvemos ok
        if (entrada.getEstado() == Estado.VENDIDA) {
            return "Entrada ya vendida anteriormente para el usuario: " + emailUsuario;
        }

        entrada.setEstado(Estado.VENDIDA);
        this.entradaDao.save(entrada);

        try {
            //Generamos el PDF de la entrada
            Espectaculo espectaculo = entrada.getEspectaculo();
            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfBytes);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.add(new Paragraph("ENTRADA ESIentradas"));
            document.add(new Paragraph("Artista: " + espectaculo.getArtista()));
            document.add(new Paragraph("Fecha: " + espectaculo.getFecha().toString()));
            document.add(new Paragraph("ID Entrada: " + entrada.getId()));
            document.add(new Paragraph("Precio: " + (entrada.getPrecio() / 100.0) + " €"));
            document.close();
            byte[] pdfBytesArray = pdfBytes.toByteArray();

            // Enviamos el email con el PDF adjunto
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

        // Eliminamos el token tras completar la compra
        this.tokenDao.deleteByValorNativo(tokenEntrada);

        return "Compra realizada con éxito para el usuario: " + emailUsuario;
    }
}

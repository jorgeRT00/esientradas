package edu.esi.ds.esientradas.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import edu.esi.ds.esientradas.model.DeZona;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Precisa;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] crearPdfEntrada(List<Entrada> entradas) {
        try {

            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfBytes);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.add(new Paragraph("ENTRADA ESIentradas"));
            document.add(new Paragraph(" "));
            for (Entrada entrada : entradas) {
                Espectaculo espectaculo = entrada.getEspectaculo();
                document.add(new Paragraph("-------------------------------"));
                document.add(new Paragraph("Artista: " + espectaculo.getArtista()));
                document.add(new Paragraph("Fecha: " + espectaculo.getFecha().toString()));
                document.add(new Paragraph("ID Entrada: " + entrada.getId()));
                document.add(new Paragraph("Precio: " + (entrada.getPrecio() / 100.0) + " euros"));
                document.add(new Paragraph(obtenerDescripcionUbicacion(entrada)));
                document.add(new Paragraph(" "));
            }
            document.close();
            return pdfBytes.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error generando PDF: " + e.getMessage(), e);
        }
    }

    private String obtenerDescripcionUbicacion(Entrada entrada) {
        if (entrada instanceof Precisa) {
            Precisa precisa = (Precisa) entrada;
            return String.format(
                    "Ubicacion: Planta %d, Fila %d, Columna %d",
                    precisa.getPlanta(),
                    precisa.getFila(),
                    precisa.getColumna());
        }
        if (entrada instanceof DeZona) {
            DeZona zona = (DeZona) entrada;
            return String.format("Ubicacion: Zona %s", zona.getZona());
        }
        return "Ubicacion: No disponible";
    }
}

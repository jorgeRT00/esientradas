package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.ReservaDao;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.model.Reserva;
import java.util.*;

@Service
public class ComprasService {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private ReservaDao reservaDao;
    @Autowired
    private EntradaDao entradaDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ZipService zipService;

    @Transactional
    public String comprar(String tokenEntrada, String tokenUsuario) {
        String emailUsuario = this.usuariosService.checkToken(tokenUsuario);
        if (emailUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no valido.");
        }

        List<Reserva> reservas = this.reservaDao.findByTokenValor(tokenEntrada);
        if (reservas == null || reservas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay reservas para ese token.");
        }

        List<Entrada> entradasCompradas = new ArrayList<>();
        for (Reserva r : reservas) {
            Entrada entrada = r.getEntrada();
            if (entrada.getEstado() == Estado.VENDIDA) {
                continue; // Omitir entradas ya vendidas
            }
            entrada.setEstado(Estado.VENDIDA);
            entrada.setEmailComprador(emailUsuario);
            this.entradaDao.save(entrada);
            entradasCompradas.add(entrada);
        }

        if (entradasCompradas.isEmpty()) {
            return "Las entradas ya estaban vendidas.";
        }

        enviarEmailCompra(emailUsuario, entradasCompradas);

        return "Compra realizada con exito para el usuario: " + emailUsuario;
    }

    private void enviarEmailCompra(String emailUsuario, List<Entrada> entradas) {
        try {
            byte[] pdfBytes = pdfService.crearPdfEntrada(entradas);
            emailService.sendEmail(
                    emailUsuario,
                    "Compra de entradas exitosa",
                    "Has comprado " + entradas.size() + " entrada(s). Adjuntamos tu(s) ticket(s).",
                    pdfBytes,
                    "entradas.pdf");
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo enviar el email a " + emailUsuario + ": " + e.getMessage());
        }
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

    public byte[] generarTicketPdf(String entradaId) {
        Entrada entrada = this.entradaDao.findById(Long.parseLong(entradaId))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada: " + entradaId));
        return pdfService.crearPdfEntrada(List.of(entrada));
    }

    public byte[] generarZipMisEntradas(String emailUsuario) {
        try {
            List<Entrada> entradas = this.entradaDao.findByEmailComprador(emailUsuario);
            Map<String, byte[]> archivos = new HashMap<>();
            for (Entrada entrada : entradas) {
                byte[] pdf = pdfService.crearPdfEntrada(List.of(entrada));
                archivos.put("entrada_" + entrada.getId() + ".pdf", pdf);
            }
            return zipService.generarZip(archivos);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error generando ZIP: " + e.getMessage(), e);
        }
    }
}

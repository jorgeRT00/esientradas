package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;
import jakarta.mail.MessagingException;

import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;

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
            emailService.sendEmail(emailUsuario, "Compra de entrada exitosa",
                    "Has comprado la entrada con ID: " + entrada.getId());
        } catch (MessagingException e) {
            System.err.println("Error al enviar el email: " + e.getMessage());
        }

        // Eliminamos el token tras completar la compra
        this.tokenDao.deleteByValorNativo(tokenEntrada);

        return "Compra realizada con éxito para el usuario: " + emailUsuario;
    }

    /**
     * Finaliza la compra desde el webhook de Stripe.
     * No envía email porque el webhook no recibe el token del usuario.
     */
    @Transactional
    public String finalizarCompraDesdeWebhook(String tokenEntrada) {
        Token token = this.tokenDao.findById(tokenEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token de entrada no encontrado."));

        Entrada entrada = token.getEntrada();

        if (entrada.getEstado() == Estado.VENDIDA) {
            this.tokenDao.deleteByValorNativo(tokenEntrada);
            return "Entrada ya vendida anteriormente.";
        }

        entrada.setEstado(Estado.VENDIDA);
        this.entradaDao.save(entrada);
        this.tokenDao.deleteByValorNativo(tokenEntrada);

        return "Compra finalizada correctamente desde webhook para la entrada: " + entrada.getId();
    }
}

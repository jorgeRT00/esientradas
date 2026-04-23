package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.dao.TokenDao;


@Service
public class ReservasService {

    @Autowired
    private UsuariosService usuariosService;
    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    @Transactional
    public Long reservar(Long entradaId, String sessionId) {
        Entrada entrada = this.entradaDao.findById(entradaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada."));
        if (entrada.getEstado() != Estado.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrada no disponible.");
        }

        // 1. Crear el token y asociarlo a la entrada y a la sesión
        Token token = new Token();
        token.setEntrada(entrada);
        token.setSession(sessionId); // Usamos el parámetro, no el texto "sessionId"

        // 2. IMPORTANTE: Guardar el token explícitamente
        this.tokenDao.save(token);

        // 3. Actualizar la entrada
        this.entradaDao.updateEstado(entradaId, Estado.RESERVADA);

        return entrada.getPrecio();
    }

    @Transactional
    public String comprar(String tokenEntrada, String tokenUsuario, String sessionId) {
        
        // 1º Veririficamos que el token de usuario llamando a esiurusuarios
        String emailUsuario = this.usuariosService.checkToken(tokenUsuario);
        if(emailUsuario == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de usuario no válido.");
        }

        // 2º Buscar el token de la entrada en la base de datos
        Token token = this.tokenDao.findById(tokenEntrada).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token de entrada no encontrado."));

        // 3º Verificar que el token de la entrada corresponde a la sesión actual
        if (!token.getSession().equals(sessionId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de entrada no válido para esta sesión.");
        }

        // 4º Marcar la entrada como vendida
        Entrada entrada = token.getEntrada();
        this.entradaDao.updateEstado(entrada.getId(), Estado.VENDIDA);

        return "Comprar realizada con éxito para el usuario: " + emailUsuario;
    }
}
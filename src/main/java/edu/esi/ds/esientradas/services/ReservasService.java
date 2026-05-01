package edu.esi.ds.esientradas.services;

import java.util.List;

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
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    @Transactional
    public String reservar(Long entradaId, String sessionId) {
        Entrada entrada = this.entradaDao.findById(entradaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada."));
        if (entrada.getEstado() != Estado.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrada no disponible.");
        }

        // 1. Crear el token y asociarlo a la entrada y a la sesión
        Token token = new Token();
        token.setEntrada(entrada);
        token.setSession(sessionId); // Usamos el parámetro, no el texto "sessionId"

        // 2. Guardar el token explícitamente
        this.tokenDao.save(token);

        // 3. Actualizar la entrada
        this.entradaDao.updateEstado(entradaId, Estado.RESERVADA);

        return token.getValor(); // Devolvemos el valor del token para que el frontend lo use en la compra
    }

    // Método para calcular el total a pagar por un token de reserva
    public long calcularTotalPorToken(String tokenReserva) {
        // 1. Buscar las entradas asociadas a ese token de reserva
        List<Entrada> entradasCompradas = entradaDao.findByTokenValor(tokenReserva);

        long totalCentimos = 0;
    
        for (Entrada entrada : entradasCompradas) {
            totalCentimos += entrada.getPrecio();
        }

        return totalCentimos;
    }
}
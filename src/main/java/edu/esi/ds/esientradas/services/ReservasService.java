package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.ReservaDao;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Reserva;


@Service
public class ReservasService {

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private ReservaDao reservaDao;

    @Autowired
    private EntradaDao entradaDao;

    @Transactional
    public String reservar(Long entradaId, String tokenReservaEntrada, String sessionId) {
        Entrada entrada = this.entradaDao.findById(entradaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada."));
        if (entrada.getEstado() != Estado.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrada no disponible.");
        }

        Token token;
        if (tokenReservaEntrada != null && !tokenReservaEntrada.isBlank()) {
            token = this.tokenDao.findById(tokenReservaEntrada)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de reserva no válido."));
        } else {
            token = new Token();
            token.setSession(sessionId);
            this.tokenDao.save(token);
        }

        Reserva reserva = new Reserva(token, entrada);
        this.reservaDao.save(reserva);

        this.entradaDao.updateEstado(entradaId, Estado.RESERVADA);
        
         return token.getValor(); // Devolvemos el valor del token para que el frontend lo use en la compra
    } 
        
        // Método para calcular el total a pagar por un token de reserva
    public long calcularTotalPorToken(String tokenReserva) {
        List<Reserva> reservas = this.reservaDao.findByTokenValor(tokenReserva);
        if (reservas == null || reservas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay reservas para el token especificado.");
        }
        return reservas.stream().mapToLong(reserva -> reserva.getEntrada().getPrecio()).sum();
    }
}
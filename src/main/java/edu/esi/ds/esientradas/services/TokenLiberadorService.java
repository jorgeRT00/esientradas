package edu.esi.ds.esientradas.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Token;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.ReservaDao;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.model.Reserva;

@Service
public class TokenLiberadorService {

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    @Autowired
    private ReservaDao reservaDao;

    private static final long TIEMPO_LIMITE = 2 * 60 * 1000; // 2 minutos en milisegundos

    @Scheduled(fixedRate = 60000 * 2) // Ejecutar cada 2 minutos
    @Transactional
    public void liberarTokensCaducados() {
        List<Token> tokens = tokenDao.findAll();
        long ahora = System.currentTimeMillis();

        for (Token token : tokens) {
            long tiempoTranscurrido = ahora - token.getHora(); // Tiempo en milisegundos
            if (tiempoTranscurrido > TIEMPO_LIMITE) { // Si el token ha caducado
                List<Reserva> reservas = reservaDao.findByToken(token); // Obtener reservas asociadas al token
                boolean hayReservadas = reservas.stream() // Verificar si hay reservas en estado RESERVADA
                        .anyMatch(r -> r.getEntrada().getEstado() == Estado.RESERVADA); // Si hay alguna reserva en estado RESERVADA, se liberan las entradas y se eliminan las reservas
                if (!reservas.isEmpty() && hayReservadas) { // Solo liberar si hay reservas y al menos una está en estado RESERVADA
                    for (Reserva r : reservas) { // Liberar las entradas asociadas a las reservas
                        if (r.getEntrada().getEstado() == Estado.RESERVADA) { // Solo liberar si la entrada está en estado RESERVADA
                            entradaDao.updateEstado(r.getEntrada().getId(), Estado.DISPONIBLE); // Actualizar el estado de la entrada a DISPONIBLE
                        }
                    }
                    reservaDao.deleteAll(reservas);
                    tokenDao.deleteByValorNativo(token.getValor());
                    System.out.println("Token liberado: " + token.getValor());
                }
            }
        }
    }
}

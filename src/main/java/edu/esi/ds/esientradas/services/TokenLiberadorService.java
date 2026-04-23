package edu.esi.ds.esientradas.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Token;
import jakarta.transaction.Transactional;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Estado;

@Service
public class TokenLiberadorService {

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private EntradaDao entradaDao;

    private static final long TIEMPO_LIMITE = 1 * 60 * 1000; // 1 minuto en milisegundos

    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    @Transactional
    public void liberarTokensCaducados() {
        List<Token> tokens = tokenDao.findAll();
        long ahora = System.currentTimeMillis();

        for (Token token : tokens) {
            long tiempoTranscurrido = ahora - token.getHora();
            if (tiempoTranscurrido > TIEMPO_LIMITE) {
                // La entrada vuelve a estar disponible
                entradaDao.updateEstado(token.getEntrada().getId(), Estado.DISPONIBLE);
                // Eliminar el token
                tokenDao.delete(token);
                System.out.println("Token liberado: " + token.getValor());
            }
        }
    }
}

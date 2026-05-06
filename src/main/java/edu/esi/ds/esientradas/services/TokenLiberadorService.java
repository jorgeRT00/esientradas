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

    private static final long TIEMPO_LIMITE = 2 * 60 * 1000; // 2 minutos en milisegundos

    @Scheduled(fixedRate = 60000 * 2) // Ejecutar cada 2 minutos
    @Transactional
    public void liberarTokensCaducados() {
        List<Token> tokens = tokenDao.findAll();
        long ahora = System.currentTimeMillis();

        for (Token token : tokens) { // Iterar sobre cada token para verificar su tiempo de vida
            long tiempoTranscurrido = ahora - token.getHora(); // Calcular el tiempo transcurrido desde la creación del
                                                               // token
            // Solo liberamos reservas que siguen activas; si ya se vendieron, no tocamos la
            // entrada.
            if (tiempoTranscurrido > TIEMPO_LIMITE && token.getEntrada().getEstado() == Estado.RESERVADA) {
                Long entradaId = token.getEntrada().getId(); // Obtener el ID de la entrada asociada al token
                String tokenValor = token.getValor(); // Obtener el valor del token para eliminarlo posteriormente
                entradaDao.updateEstado(entradaId, Estado.DISPONIBLE); // Actualizar el estado de la entrada a
                                                                       // DISPONIBLE para que pueda ser utilizada por
                                                                       // otros usuarios
                tokenDao.deleteByValorNativo(tokenValor); // Eliminar el token utilizando el método definido en TokenDao
                System.out.println("Token liberado: " + token.getValor());
            }
        }
    }
}

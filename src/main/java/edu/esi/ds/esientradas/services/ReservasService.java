package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import jakarta.transaction.Transactional;

@Service
public class ReservasService {

    @Autowired
    private EntradaDao entradaDao;

    @Autowired
    private TokenDao tokenDao;

@Transactional
public Long reservar(Long idEntrada, String sessionId) {
    // Spring abre la transacción aquí ─────────────────────────────────────────┐
                                                                                //│
    Entrada entrada = this.entradaDao.findById(idEntrada).orElseThrow(          //│ SELECT → busca la entrada en BD
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,                 //│
            "Entrada no encontrada")                                             //│ → ROLLBACK si no existe
    );                                                                          //│
                                                                                //│
    if (entrada.getEstado() != Estado.DISPONIBLE) {                             //│ comprueba disponibilidad
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,              //│
            "Entrada no disponible");                                            //│ → ROLLBACK si no disponible
    }                                                                           //│          
                                                                                //│
    /* ALTERNATIVA con save() — carga el objeto completo en memoria:            //│
    entrada.setEstado(Estado.RESERVADA);                                        //│
    this.entradaDao.save(entrada);                                              //│
    */                                                                          //│

    Token token = new Token();                                                  //│ Crea token de reserva
    //token.setEntrada(entrada);
    //this.tokenDao.save(token);                                                //│ Guarda token en BD (genera valor del token)                                                                            //| Importar Token del de nuestro modelo */
    entrada.setTokenReserva(token);                                             //│ Asocia token a la entrada (Entrada es propietario)
    token.setEntrada(entrada);                                                 //│ Relación inversa (bidireccional) Para asegurar 1 a 1.
    this.entradaDao.save(entrada);                                              //│ Guarda entrada en BD con estado RESERVADA y token asociado (genera id de la entrada si no lo tenía)                                                                                   //| Importar Entrada del de nuestro modelo */
    
                                                                                this.entradaDao.updateEstado(idEntrada, Estado.RESERVADA);                  //│ UPDATE directo en BD (más eficiente)
    return entrada.getPrecio();                                                 //│ devuelve precio
    // Spring hace COMMIT aquí (todo OK) ──────────────────────────────────────┘
}
}

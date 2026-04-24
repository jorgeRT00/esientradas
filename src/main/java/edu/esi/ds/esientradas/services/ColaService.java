package edu.esi.ds.esientradas.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import edu.esi.ds.esientradas.dao.ColaEsperaDao;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import edu.esi.ds.esientradas.model.ColaEspera;
import edu.esi.ds.esientradas.model.Espectaculo;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.dao.EspectaculoDao;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ColaService {

    @Autowired
    private ColaEsperaDao colaEsperaDao;

    @Autowired
    private EspectaculoDao espectaculoDao;

    @Transactional
    public String unirse(Long espectaculoId, String emailUsuario) {

        Espectaculo espectaculo = espectaculoDao.findById(espectaculoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado.") );
    
        //Comproabr que el espectaculo tiene cola
        if (espectaculo.getFechaAperturaTaquilla() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El espectaculo no tiene cola de espera.");
        }

        //Comprobar que el usuario no esta ya en la cola de espera
        if (colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya esta en la cola de espera.");
        }

        //Obtener la posicion del usuario en la cola de espera
        List<ColaEspera> cola = colaEsperaDao.findByEspectaculoOrderByPosicionAsc(espectaculo);
        int posicion = cola.isEmpty() ? 1 : cola.get(cola.size() - 1).getPosicion() + 1;

        ColaEspera entrada = new ColaEspera();
        entrada.setEspectaculo(espectaculo);
        entrada.setEmailUsuario(emailUsuario);
        entrada.setPosicion(posicion);
        entrada.setHoraEntrada(java.time.LocalDateTime.now());
        colaEsperaDao.save(entrada);

        return "Te has unido a la cola en la posicion " + posicion;
    }

    public int consultarPosicion(Long espectaculoId, String emailUsuario) {
        Espectaculo espectaculo = espectaculoDao.findById(espectaculoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado.") );
        
        ColaEspera entrada = colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en la cola de espera.") );

        return colaEsperaDao.countByEspectaculoAndPosicionLessThan(espectaculo, entrada.getPosicion());
    }

    // Se ejecuta cada 1 minuto: libera turnos que llevan mas de 5 minutos esperando y asigna entradas a los siguientes usuarios en la cola de espera
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void liberarTurnosCaducados() {
        List<ColaEspera> cola = colaEsperaDao.findAll();
        LocalDateTime limite = LocalDateTime.now().minusMinutes(5);

        for (ColaEspera entrada : cola) {
            if (entrada.getHoraInicioTurno() != null && entrada.getHoraInicioTurno().isBefore(limite)) {
                System.out.println("Liberando turno del usuario " + entrada.getEmailUsuario() + " para el espectaculo " + entrada.getEspectaculo().getId());
                colaEsperaDao.delete(entrada);
            }
        }
    }
}   

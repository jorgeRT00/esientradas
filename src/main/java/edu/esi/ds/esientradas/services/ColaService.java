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
    private jakarta.persistence.EntityManager entityManager;

    @Autowired
    private ColaEsperaDao colaEsperaDao;

    @Autowired
    private EspectaculoDao espectaculoDao;

    @Transactional
    public String unirse(Long espectaculoId, String emailUsuario) {

        Espectaculo espectaculo = espectaculoDao.findById(espectaculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado."));

        // Comproabr que el espectaculo tiene cola
        if (espectaculo.getFechaAperturaTaquilla() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El espectaculo no tiene cola de espera.");
        }

        // Comprobar que la taquilla aun no esta abierta
        if (java.time.ZonedDateTime.now(java.time.ZoneId.of("Europe/Madrid"))
                .isAfter(espectaculo.getFechaAperturaTaquilla().atZone(java.time.ZoneId.of("Europe/Madrid")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La taquilla ya esta abierta. Accede directamente a comprar entradas.");
        }

        // Comprobar que el usuario no esta ya en la cola de espera
        if (colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya esta en la cola de espera.");
        }

        // Obtener la posicion del usuario en la cola de espera
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado."));

        ColaEspera entrada = colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado en la cola de espera."));

        return colaEsperaDao.countByEspectaculoAndPosicionLessThan(espectaculo, entrada.getPosicion());
    }

    public boolean tieneTurno(Long espectaculoId, String emailUsuario) {
        Espectaculo espectaculo = espectaculoDao.findById(espectaculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado."));

        return colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario)
                .map(e -> e.getHoraInicioTurno() != null).orElse(false);
    }

    @Transactional
    public void salirDeCola(Long espectaculoId, String emailUsuario) {
        Espectaculo espectaculo = espectaculoDao.findById(espectaculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado."));

        colaEsperaDao.findByEspectaculoAndEmailUsuario(espectaculo, emailUsuario).ifPresent(e -> {
            colaEsperaDao.delete(e);
            activarSiguiente(espectaculo);
        });
    }

    // Se ejecuta cada 10 segundos: libera turnos que llevan mas de 5 minutos
    // esperando y asigna entradas a los siguientes usuarios en la cola de espera
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void gestionarColas() {
        for (Espectaculo espectaculo : espectaculoDao.findByFechaAperturaTaquillaIsNotNull()) {
            LocalDateTime limite = LocalDateTime.now(java.time.ZoneId.of("Europe/Madrid")).minusMinutes(1);

            colaEsperaDao.findByEspectaculoOrderByPosicionAsc(espectaculo).stream()
                    .filter(e -> e.getHoraInicioTurno() != null && e.getHoraInicioTurno().isBefore(limite))
                    .forEach(e -> colaEsperaDao.delete(e));
            entityManager.flush(); // Asegura que se apliquen los cambios antes de activar el siguiente
            entityManager.clear(); // Limpia el contexto de persistencia para evitar problemas de caché

            activarSiguiente(espectaculo);
        }
    }

    private void activarSiguiente(Espectaculo espectaculo) {
        if (LocalDateTime.now(java.time.ZoneId.of("Europe/Madrid")).isBefore(espectaculo.getFechaAperturaTaquilla()))
            return;

        boolean hayActivo = colaEsperaDao.findByEspectaculoOrderByPosicionAsc(espectaculo)
                .stream().anyMatch(e -> e.getHoraInicioTurno() != null);

        if (!hayActivo)
            colaEsperaDao.findFirstByEspectaculoAndHoraInicioTurnoIsNullOrderByPosicionAsc(espectaculo)
                    .ifPresent(e -> {
                        e.setHoraInicioTurno(LocalDateTime.now());
                        colaEsperaDao.save(e);
                    });
    }
}
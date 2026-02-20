package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.model.Escenario;
import org.springframework.http.HttpStatus;

@Service

public class EscenariosService {

    @Autowired
    private EscenarioDao escenarioDao;

    public void insertarEscenario(Escenario escenario) {
        try {
            this.escenarioDao.save(escenario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ya existe un escenario con el mismo nombre, no se pueden insertar escenarios con el mismo nombre", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No sabemos que ha pasado, pero hubo error " + e.getMessage(), e);
        }
    }
}

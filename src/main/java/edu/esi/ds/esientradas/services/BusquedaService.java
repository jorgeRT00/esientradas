package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.dao.EscenarioDao;

@Service
public class BusquedaService {

    @Autowired
    private EscenarioDao escenarioDao;

    public List<Escenario> getEscenarios() {
        // aqui se haria la logica para obtener los escenarios de la base de datos
        return this.escenarioDao.findAll(); // se devuelve la lista de escenarios obtenida del DAO
    }


}

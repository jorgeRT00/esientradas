package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.dao.EscpectaculoDao;
import edu.esi.ds.esientradas.dao.EntradaDao;

@Service
public class BusquedaService {

    @Autowired
    private EscenarioDao escenarioDao;
    
    @Autowired
    private EscpectaculoDao espectaculoDao;

    @Autowired
    private EntradaDao entradaDao;

    public List<Entrada> getEntradas(String espectaculoId) {
        // aqui se haria la logica para obtener las entradas de la base de datos
        return this.entradaDao.findByEspectaculoId(Long.parseLong(espectaculoId)); // se devuelve la lista de entradas obtenida del DAO
    }

    public List<Escenario> getEscenarios() {
        // aqui se haria la logica para obtener los escenarios de la base de datos
        return this.escenarioDao.findAll(); // se devuelve la lista de escenarios obtenida del DAO
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        // aqui se haria la logica para obtener los espectaculos de la base de datos
        return this.espectaculoDao.findByArtista(artista); // se devuelve la lista de espectaculos obtenida del DAO
    }


}

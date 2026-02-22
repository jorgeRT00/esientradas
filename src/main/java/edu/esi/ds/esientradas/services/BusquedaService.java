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

@Service /* Le dice a Spring que esta clase es un servicio, y que debe ser gestionada por el contenedor de Spring. 
                Un servicio es una clase que contiene la lógica de negocio de la aplicación, y que se encarga de interactuar con los DAOs para obtener los datos de la base de datos. */
public class BusquedaService {

    @Autowired /* Le dice a Spring que inyecte una instancia de EscenarioDao en esta clase. 
                Spring buscará una clase que implemente la interfaz EscenarioDao, y creará una instancia de esa clase para inyectarla en esta clase. */
    private EscenarioDao escenarioDao;
    
    @Autowired
    private EscpectaculoDao espectaculoDao;

    @Autowired
    private EntradaDao entradaDao;

    public List<Entrada> getEntradas(String espectaculoId) {
        // aqui se haria la logica para obtener las entradas de la base de datos
        return this.entradaDao.findByEspectaculoId(Long.parseLong(espectaculoId)); // se devuelve la lista de entradas obtenida del DAO
                // Long.parseLong(espectaculoId) se utiliza para convertir el String espectaculoId a un Long, ya que el método findByEspectaculoId espera un Long como parámetro.
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

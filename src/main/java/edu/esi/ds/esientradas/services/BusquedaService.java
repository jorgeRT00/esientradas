package edu.esi.ds.esientradas.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.dao.EspectaculoDao;
import edu.esi.ds.esientradas.dto.DtoEntradas;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dao.EntradaDao;

@Service
public class BusquedaService {

    @Autowired
    private EscenarioDao escenarioDao;

    @Autowired
    private EspectaculoDao espectaculoDao;

    @Autowired
    private EntradaDao entradaDao;
    
    @Autowired
    private UbicacionMapper ubicacionMapper;

    public List<Entrada> getEntradas(Long espectaculoId) {
        return this.entradaDao.findByEspectaculoId(espectaculoId); // se devuelve la lista de entradas obtenida del DAO
    }

    /**
     * Obtiene las entradas de un espectáculo con ubicación formateada (como DTOs).
     * 
     * @param espectaculoId ID del espectáculo
     * @return Lista de EntradaDTO con ubicación formateada
     */
    public List<EntradaDTO> getEntradasDTO(Long espectaculoId) {
        List<Entrada> entradas = this.entradaDao.findByEspectaculoId(espectaculoId);
        return entradas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo entradas DISPONIBLES con ubicación formateada (DTOs).
     */
    public List<EntradaDTO> getEntradasDisponiblesDTO(Long espectaculoId) {
        List<Entrada> entradas = this.entradaDao.findByEspectaculoIdAndEstado(espectaculoId, Estado.DISPONIBLE);
        return entradas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una Entrada a EntradaDTO con ubicación formateada.
     */
    private EntradaDTO convertirADTO(Entrada entrada) {
        return new EntradaDTO(
            entrada.getId(),
            entrada.getPrecio(),
            entrada.getEstado().toString(),
            entrada.getEmailComprador(),
            entrada.getEspectaculo().getArtista(),
            ubicacionMapper.mapearUbicacion(entrada)
        );
    }

    public List<Escenario> getEscenarios() {
        return this.escenarioDao.findAll(); // se devuelve la lista de escenarios obtenida del DAO
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        if (artista == null || artista.isBlank()) {
            return this.espectaculoDao.findAll(); // si el artista es nulo o vacío, se devuelve la lista de todos los espectaculos
        }
        return this.espectaculoDao.findByArtista(artista); // se devuelve la lista de espectaculos obtenida del DAO
    }

    public List<Espectaculo> getEspectaculos(Long escenarioId) {
        return this.espectaculoDao.findByEscenarioId(escenarioId); // se devuelve la lista de espectaculos obtenida del DAO
    }

    public DtoEntradas getNumeroEntradasDto(Long espectaculoId) {
        return this.entradaDao.getNumeroEntradasDT(espectaculoId);
    }

    public Integer getNumeroEntradas(Long espectaculoId) {
        DtoEntradas dto = this.entradaDao.getNumeroEntradasDT(espectaculoId);
        return dto.getTotales();
    }

    public Integer getEntradasLibres(Long espectaculoId) {
        DtoEntradas dto = this.entradaDao.getNumeroEntradasDT(espectaculoId);
        return dto.getLibres();
    }

}

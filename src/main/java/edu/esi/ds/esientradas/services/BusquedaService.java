package edu.esi.ds.esientradas.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.dao.EspectaculoDao;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dto.EntradasYEscenarioDTO;
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

    /**
     * Obtiene solo entradas DISPONIBLES con ubicación formateada (DTOs).
     */
    public List<EntradaDTO> getEntradasDisponiblesDTO(Long espectaculoId) {
        List<Entrada> entradas = this.entradaDao.findByEspectaculoId(espectaculoId);
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
        return this.escenarioDao.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        if (artista == null || artista.isBlank()) {
            return this.espectaculoDao.findAll();
        }
        return this.espectaculoDao.findByArtistaContainingIgnoreCase(artista.trim());
    }

    public List<Espectaculo> getEspectaculos(Long escenarioId) {
        return this.espectaculoDao.findByEscenarioId(escenarioId);
    }

    
    // Obtiene las entradas disponibles junto con el tipo de escenario del espectáculo.
    // Usado para que el frontend sepa si mostrar interfaz de ZONAS o BUTACAS.
     
    public EntradasYEscenarioDTO getEntradasDisponiblesConEscenario(Long espectaculoId) {
        Espectaculo espectaculo = this.espectaculoDao.findById(espectaculoId)
            .orElseThrow(() -> new IllegalArgumentException("Espectáculo no encontrado: " + espectaculoId));

        String tipoEscenario = "DESCONOCIDO";
        if (espectaculo.getEscenario() != null && espectaculo.getEscenario().getTipo() != null) {
            tipoEscenario = espectaculo.getEscenario().getTipo().name();
        }

        List<EntradaDTO> entradas = this.getEntradasDisponiblesDTO(espectaculoId);
        return new EntradasYEscenarioDTO(tipoEscenario, entradas);
    }
}

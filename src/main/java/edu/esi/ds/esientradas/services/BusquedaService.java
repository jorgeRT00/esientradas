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
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dto.EspectaculoDTO;
import edu.esi.ds.esientradas.dto.EscenarioDTO;
import edu.esi.ds.esientradas.dto.EstadisticasEspectaculoDTO;

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

    // Todas las entradas de un espectáculo como DTOs
    public List<EntradaDTO> getEntradasDTO(Long espectaculoId) {
        return entradaDao.findByEspectaculoId(espectaculoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Solo las entradas DISPONIBLES como DTOs
    public List<EntradaDTO> getEntradasDisponiblesDTO(Long espectaculoId) {
        return entradaDao.findByEspectaculoIdAndEstado(espectaculoId, Estado.DISPONIBLE)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private EntradaDTO convertirADTO(Entrada entrada) {
        String tipoEscenario = "DESCONOCIDO";
        if (entrada.getEspectaculo() != null &&
                entrada.getEspectaculo().getEscenario() != null &&
                entrada.getEspectaculo().getEscenario().getTipo() != null) {
            tipoEscenario = entrada.getEspectaculo().getEscenario().getTipo().name();
        }
        return new EntradaDTO(
                entrada.getId(),
                entrada.getPrecio(),
                entrada.getEstado().toString(),
                entrada.getEmailComprador(),
                entrada.getEspectaculo().getArtista(),
                ubicacionMapper.mapearUbicacion(entrada),
                tipoEscenario);
    }

    public List<Escenario> getEscenarios() {
        return escenarioDao.findAll();
    }

    public List<EspectaculoDTO> getEspectaculos(String artista) {
        List<Espectaculo> lista = (artista == null || artista.isBlank())
                ? espectaculoDao.findAll()
                : espectaculoDao.findByArtistaContainingIgnoreCase(artista.trim());
        return lista.stream().map(this::convertirEspectaculoADto).collect(Collectors.toList());
    }

    public List<EspectaculoDTO> getEspectaculos(Long escenarioId) {
        return espectaculoDao.findByEscenarioId(escenarioId)
                .stream()
                .map(this::convertirEspectaculoADto)
                .collect(Collectors.toList());
    }

    private EspectaculoDTO convertirEspectaculoADto(Espectaculo e) {
        EspectaculoDTO dto = new EspectaculoDTO();
        dto.setId(e.getId());
        dto.setArtista(e.getArtista());
        dto.setFecha(e.getFecha());
        dto.setFechaAperturaTaquilla(e.getFechaAperturaTaquilla());
        if (e.getEscenario() != null) {
            String nombre = e.getEscenario().getNombre() != null ? e.getEscenario().getNombre() : "Desconocido";
            String tipo = e.getEscenario().getTipo() != null ? e.getEscenario().getTipo().name() : "DESCONOCIDO";
            dto.setEscenario(new EscenarioDTO(nombre, tipo));
        } else {
            dto.setEscenario(new EscenarioDTO("Desconocido", "DESCONOCIDO"));
        }
        return dto;
    }

    public EstadisticasEspectaculoDTO getNumeroEntradasDto(Long espectaculoId) {
        return entradaDao.getNumeroEntradasDT(espectaculoId);
    }
}
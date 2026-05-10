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
import edu.esi.ds.esientradas.dto.EstadisticasEspectaculoDTO;
import edu.esi.ds.esientradas.dto.EntradaDTO;
import edu.esi.ds.esientradas.dao.EntradaDao;
// IMPORTAMOS LOS DTO DE ESPECTÁCULO (Vital para que el Controller no falle)
import edu.esi.ds.esientradas.dto.EspectaculoDTO; 
import edu.esi.ds.esientradas.dto.EscenarioDTO;   

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

    public List<EntradaDTO> getEntradasDTO(Long espectaculoId) {
        List<Entrada> entradas = this.entradaDao.findByEspectaculoId(espectaculoId);
        return entradas.stream()
                .map(this::convertirADTO) // Ahora sí lo va a encontrar
                .collect(Collectors.toList());
    }

    public List<EntradaDTO> getEntradasDisponiblesDTO(Long espectaculoId) {
        List<Entrada> entradas = this.entradaDao.findByEspectaculoId(espectaculoId);
        return entradas.stream()
                .map(this::convertirADTO) // Ahora sí lo va a encontrar
                .collect(Collectors.toList());
    }

    // --- ¡ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ROJO! ---
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
            tipoEscenario,
            ubicacionMapper.mapearUbicacion(entrada)
        );
    }

    public List<Escenario> getEscenarios() {
        return this.escenarioDao.findAll(); 
    }

    // --- ESTOS MÉTODOS AHORA DEVUELVEN DTOs AL CONTROLLER ---
    public List<EspectaculoDTO> getEspectaculos(String artista) {
        List<Espectaculo> listaBD;
        if (artista == null || artista.isBlank()) {
            listaBD = this.espectaculoDao.findAll(); 
        } else {
            listaBD = this.espectaculoDao.findByArtistaContainingIgnoreCase(artista.trim()); 
        }
        return listaBD.stream().map(this::convertirEspectaculoADto).collect(Collectors.toList());
    }

    public List<EspectaculoDTO> getEspectaculos(Long escenarioId) {
        List<Espectaculo> listaBD = this.espectaculoDao.findByEscenarioId(escenarioId); 
        return listaBD.stream().map(this::convertirEspectaculoADto).collect(Collectors.toList());
    }

    // --- ESTE ES EL CÓDIGO QUE SACAMOS DEL CONTROLLER ---
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

    // --- ESTADÍSTICAS ---
    public EstadisticasEspectaculoDTO getNumeroEntradasDto(Long espectaculoId) {
        return this.entradaDao.getNumeroEntradasDT(espectaculoId);
    }

}
package edu.esi.ds.esientradas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.Espectaculo;

public interface EspectaculoDao extends JpaRepository<Espectaculo, Long> {

    List<Espectaculo> findByArtista(String artista); // Buscar por artista exacto
    List<Espectaculo> findByArtistaContainingIgnoreCase(String artista); // Buscar por artista parcial

    List<Espectaculo> findByEscenarioId(Long escenarioId); // Buscar por escenario

    List<Espectaculo> findByFechaAperturaTaquillaIsNotNull(); // Buscar por espectaculos con taquilla abierta

}

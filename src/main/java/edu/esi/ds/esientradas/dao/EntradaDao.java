package edu.esi.ds.esientradas.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.Entrada;

public interface EntradaDao extends JpaRepository<Entrada, Long> {
    
    // Spring Data JPA creará la consulta SQL automáticamente al ver este nombre
    List<Entrada> findByEspectaculoId(Long espectaculoId);
}
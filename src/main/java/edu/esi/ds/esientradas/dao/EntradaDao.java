package edu.esi.ds.esientradas.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import edu.esi.ds.esientradas.dto.EstadisticasEspectaculoDTO;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;

public interface EntradaDao extends JpaRepository<Entrada, Long> {

    List<Entrada> findByEmailComprador(String emailComprador);

    List<Entrada> findByEspectaculoId(Long espectaculoId);

    List<Entrada> findByEspectaculoIdAndEstado(Long espectaculoId, Estado estado);

    List<Entrada> findByTokenValor(String valor);

    @Query(value = "SELECT DISTINCT dtype FROM entrada", nativeQuery = true)
    List<String> obtenerTiposEntrada();

    @Query(value = "UPDATE Entrada e SET e.estado = :estado WHERE e.id = :entradaId")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void updateEstado(@Param("entradaId") Long entradaId, @Param("estado") Estado estado);

    @Query("""
                SELECT new edu.esi.ds.esientradas.dto.EstadisticasEspectaculoDTO(
                    COUNT(e),
                    SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.DISPONIBLE THEN 1L ELSE 0L END),
                    SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.VENDIDA THEN 1L ELSE 0L END),
                    SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.RESERVADA THEN 1L ELSE 0L END)
                )
                FROM Entrada e
                WHERE e.espectaculo.id = :espectaculoId
            """)
    EstadisticasEspectaculoDTO getNumeroEntradasDT(@Param("espectaculoId") Long espectaculoId);
}
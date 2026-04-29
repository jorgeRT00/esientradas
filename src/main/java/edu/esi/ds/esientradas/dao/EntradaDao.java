package edu.esi.ds.esientradas.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import edu.esi.ds.esientradas.dto.DtoEntradas;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;

public interface EntradaDao extends JpaRepository<Entrada, Long> {

    List<Entrada> findByEspectaculoId(Long espectaculoId);
    List<Entrada> findByTokenValor(String valor); // consulta SQL: SELECT * FROM entrada WHERE token_reserva = ?
    @Query(value = "UPDATE Entrada e SET e.estado = :estado WHERE e.id = :entradaId") // Consulta JPQL para actualizar el estado de una entrada
    @Modifying
    void updateEstado(@Param("entradaId") Long entradaId, @Param("estado") Estado estado); // Método para actualizar el estado de una entrada

    @Query("""
        SELECT new edu.esi.ds.esientradas.dto.DtoEntradas(
            COUNT(e), 
            SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.DISPONIBLE THEN 1L ELSE 0L END),
            SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.VENDIDA THEN 1L ELSE 0L END),
            SUM(CASE WHEN e.estado = edu.esi.ds.esientradas.model.Estado.RESERVADA THEN 1L ELSE 0L END)
        )
        FROM Entrada e
        WHERE e.espectaculo.id = :espectaculoId
    """)
    DtoEntradas getNumeroEntradasDT(@Param("espectaculoId") Long espectaculoId);
}

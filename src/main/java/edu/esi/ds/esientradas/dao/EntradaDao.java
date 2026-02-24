package edu.esi.ds.esientradas.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;


public interface EntradaDao extends JpaRepository<Entrada, Long> {
    // Spring Data JPA creará la consulta SQL automáticamente al ver este nombre
    List<Entrada> findByEspectaculoId(Long espectaculoId);
    
    /* Ver lo de value = y ,nativeQuery=true.
       Ver #Modifying y @Query */
    @Query(value = "UPDATE Entrada e SET e.estado = :estado WHERE e.id = :idEntrada") // EJEMPLO DE CONSULTA SQL PARA ACTUALIZAR EL ESTADO DE UNA ENTRADA, SIN NECESIDAD DE CARGARLA COMPLETA EN MEMORIA
    @Modifying // ESTA ANOTACIÓN ES NECESARIA PARA INDICAR QUE ESTA CONSULTA ES DE MODIFICACIÓN de la BBDD, Y NO DE SELECCIÓN
    void updateEstado(@Param("idEntrada") Long idEntrada, @Param("estado") Estado estado); /* @Param ES NECESARIO PARA INDICAR LOS PARÁMETROS DE LA CONSULTA SQL, Y ASOCIARLOS CON LOS PARÁMETROS DEL MÉTODO. 
                                                                                         EN ESTE CASO, SE ASOCIA EL PARÁMETRO idEntrada DE LA CONSULTA SQL CON EL PARÁMETRO idEntrada DEL MÉTODO, Y EL PARÁMETRO estado DE LA CONSULTA SQL CON EL PARÁMETRO estado DEL MÉTODO. 
                                                                                         Se podian haber llamado de forma distinta */
}

/* ES UNA INTERFAZ, NO TIENE IMPLEMENTACIÓN */
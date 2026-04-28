package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.ColaEspera;
import edu.esi.ds.esientradas.model.Espectaculo;
import java.util.List;
import java.util.Optional;

public interface ColaEsperaDao extends JpaRepository<ColaEspera, Long> {

    // Todos los usuraios en la cola de espera de un espectaculo ordenados por su
    // posicion
    List<ColaEspera> findByEspectaculoOrderByPosicionAsc(Espectaculo espectaculo);

    // Bucar si un usuario esta en la cola de espera de un espectaculo
    Optional<ColaEspera> findByEspectaculoAndEmailUsuario(Espectaculo espectaculo, String emailUsuario);

    // Contar cuantos hay delanete de un usuario.
    int countByEspectaculoAndPosicionLessThan(Espectaculo espectaculo, int posicion);

    // El primero en la cola que aun no tiene turno asignado
    Optional<ColaEspera> findFirstByEspectaculoAndHoraInicioTurnoIsNullOrderByPosicionAsc(Espectaculo espectaculo);

}

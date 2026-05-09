package edu.esi.ds.esientradas.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.Reserva;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;

public interface ReservaDao extends JpaRepository<Reserva, Long> {

    List<Reserva> findByToken(Token token);

    List<Reserva> findByTokenValor(String tokenValor);

    boolean existsByEntrada(Entrada entrada);

}
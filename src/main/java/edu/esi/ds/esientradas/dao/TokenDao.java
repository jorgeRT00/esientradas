package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.Token;
import java.util.Optional;

public interface TokenDao extends JpaRepository<Token, String> {

    Optional<Token> findByValor(String valor);

}

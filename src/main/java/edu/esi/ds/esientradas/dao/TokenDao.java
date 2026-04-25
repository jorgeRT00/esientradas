package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esientradas.model.Token;
import java.util.Optional;

public interface TokenDao extends JpaRepository<Token, String> {

    Optional<Token> findByValor(String valor);

    @Modifying // Indica que este método realiza una operación de modificación (DELETE)
    @Query(value = "DELETE FROM token WHERE valor = :valor", nativeQuery = true) // Consulta SQL nativa para eliminar un token por su valor
    void deleteByValorNativo(@Param("valor") String valor); // Parámetro para el valor del token a eliminar

}

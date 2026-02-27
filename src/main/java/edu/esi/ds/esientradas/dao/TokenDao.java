package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import edu.esi.ds.esientradas.model.Entrada;


public interface TokenDao extends JpaRepository<Entrada, Long> {

}

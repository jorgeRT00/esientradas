package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.esi.ds.esientradas.model.Pago;
import java.util.Optional;


public interface PagoDao extends JpaRepository<Pago, Long> {

	Optional<Pago> findByStripePaymentIntentId(String stripePaymentIntentId);
    

}

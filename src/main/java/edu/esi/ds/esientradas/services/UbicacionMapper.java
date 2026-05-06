package edu.esi.ds.esientradas.services;

import java.util.Map;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Entrada;

/**
 * Servicio que transforma una Entrada en su ubicación formateada.
 * 
 * PROPÓSITO: Lograr POLIMORFISMO REAL.
 * Ya no usamos 'instanceof'. Delegamos en la propia entrada para que 
 * devuelva sus datos según su tipo específico.
 */
@Service
public class UbicacionMapper {

    public Map<String, Object> mapearUbicacion(Entrada entrada) {
        if (entrada == null) {
            return Map.of(
                "tipo", "DESCONOCIDO", 
                "descripcion", "Entrada nula o no encontrada"
            );
        }
        
        // Java sabe automáticamente si ejecutar el método de Precisa o el de DeZona
        return entrada.getUbicacionAsMap();
    }
}
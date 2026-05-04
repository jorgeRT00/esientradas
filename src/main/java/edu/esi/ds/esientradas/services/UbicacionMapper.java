package edu.esi.ds.esientradas.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.DeButaca;
import edu.esi.ds.esientradas.model.DeZona;

/**
 * Servicio que transforma una Entrada en su ubicación formateada.
 * 
 * PROPÓSITO: Lograr POLIMORFISMO - tratar diferente a DeButaca y DeZona
 * sin escribir código en el controller.
 * 
 * ¿Cómo funciona?
 * 1. Recibe una Entrada (puede ser DeButaca o DeZona)
 * 2. Pregunta: ¿De qué tipo eres? (instanceof)
 * 3. Extrae los datos específicos
 * 4. Crea un Map con la estructura apropiada
 * 5. Devuelve el Map formateado
 */
@Service
public class UbicacionMapper {

    /**
     * Transforma una Entrada en su ubicación formateada.
     * 
     * @param entrada La entrada (DeButaca o DeZona)
     * @return Map con la estructura de ubicación
     */
    public Map<String, Object> mapearUbicacion(Entrada entrada) {
        
        // ¿Qué tipo de entrada es?
        if (entrada instanceof DeButaca) {
            // Es un teatro con coordenadas
            return mapearButaca((DeButaca) entrada);
        } 
        else if (entrada instanceof DeZona) {
            // Es concierto/estadio con zonas
            return mapearZona((DeZona) entrada);
        }
        
        // Si no es ninguno de los dos, retornar genérico
        return crearUbicacionGeneral();
    }

    /**
     * Mapea una DeButaca (teatro).
     * 
     * Estructura del resultado:
     * {
     *   "tipo": "BUTACA",
     *   "planta": 1,
     *   "fila": 5,
     *   "butaca": 12,
     *   "descripcion": "Planta 1, Fila 5, Butaca 12"
     * }
     */
    private Map<String, Object> mapearButaca(DeButaca butaca) {
        Map<String, Object> ubicacion = new HashMap<>();
        
        // Información específica de butaca
        ubicacion.put("tipo", "BUTACA");
        ubicacion.put("planta", butaca.getPlanta());
        ubicacion.put("fila", butaca.getFila());
        ubicacion.put("butaca", butaca.getButaca());
        
        // Descripción legible
        String descripcion = String.format(
            "Planta %d, Fila %d, Butaca %d",
            butaca.getPlanta(),
            butaca.getFila(),
            butaca.getButaca()
        );
        ubicacion.put("descripcion", descripcion);
        
        return ubicacion;
    }

    /**
     * Mapea una DeZona (concierto/estadio).
     * 
     * Estructura del resultado:
     * {
     *   "tipo": "ZONA",
     *   "zona": "Pista",
     *   "descripcion": "Zona: Pista"
     * }
     */
    private Map<String, Object> mapearZona(DeZona zona) {
        Map<String, Object> ubicacion = new HashMap<>();
        
        // Información específica de zona
        ubicacion.put("tipo", "ZONA");
        ubicacion.put("zona", zona.getZona());
        
        // Descripción legible
        String descripcion = String.format("Zona: %s", zona.getZona());
        ubicacion.put("descripcion", descripcion);
        
        return ubicacion;
    }

    /**
     * Ubicación genérica (fallback).
     */
    private Map<String, Object> crearUbicacionGeneral() {
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("tipo", "DESCONOCIDO");
        ubicacion.put("descripcion", "Ubicación no especificada");
        return ubicacion;
    }
}

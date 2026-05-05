package edu.esi.ds.esientradas.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Precisa;
import edu.esi.ds.esientradas.model.DeZona;

/**
 * Servicio que transforma una Entrada en su ubicación formateada.
 * 
 * PROPÓSITO: Lograr POLIMORFISMO - tratar diferente a Precisa y DeZona
 * sin escribir código en el controller.
 * 
 * ¿Cómo funciona?
 * 1. Recibe una Entrada (puede ser Precisa o DeZona)
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
     * @param entrada La entrada (Precisa o DeZona)
     * @return Map con la estructura de ubicación
     */
    public Map<String, Object> mapearUbicacion(Entrada entrada) {
        
        // ¿Qué tipo de entrada es?
        if (entrada instanceof Precisa) {
            // Es un teatro con coordenadas (planta/fila/columna)
            return mapearButaca((Precisa) entrada);
        } 
        else if (entrada instanceof DeZona) {
            // Es concierto/estadio con zonas
            return mapearZona((DeZona) entrada);
        }
        
        // Si no es ninguno de los dos, es un caso anómalo: la fila existe,
        // pero no está clasificada como butaca ni como zona.
        return crearUbicacionGeneral();
    }

    /**
     * Mapea una Precisa (teatro).
     * 
     * Estructura del resultado:
     * {
     *   "tipo": "BUTACA",
     *   "planta": 0,
     *   "fila": 5,
     *   "columna": 12,
     *   "descripcion": "Planta 0, Fila 5, Columna 12"
     * }
     */
    private Map<String, Object> mapearButaca(Precisa precisa) {
        Map<String, Object> ubicacion = new HashMap<>();
        
        // Información específica de butaca (coordinadas: planta, fila, columna)
        ubicacion.put("tipo", "BUTACA");
        ubicacion.put("planta", precisa.getPlanta());
        ubicacion.put("fila", precisa.getFila());
        ubicacion.put("columna", precisa.getColumna());
        
        // Descripción legible; si faltan coordenadas, lo indicamos explícitamente.
        String descripcion = tieneCoordenadasButacaCompletas(precisa)
            ? String.format(
                "Planta %d, Fila %d, Columna %d",
                precisa.getPlanta(),
                precisa.getFila(),
                precisa.getColumna()
            )
            : "Ubicación de butaca pendiente de completar";
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
        
        // Descripción legible; si falta la zona, lo indicamos explícitamente.
        String descripcion = tieneZonaValida(zona)
            ? String.format("Zona: %s", zona.getZona())
            : "Ubicación de zona pendiente de completar";
        ubicacion.put("descripcion", descripcion);
        
        return ubicacion;
    }

    /**
     * Ubicación genérica (fallback).
     */
    private Map<String, Object> crearUbicacionGeneral() {
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("tipo", "DESCONOCIDO");
        ubicacion.put("descripcion", "Ubicación pendiente de asignación");
        return ubicacion;
    }

    private boolean tieneCoordenadasButacaCompletas(Precisa precisa) {
        return precisa.getPlanta() != 0 && precisa.getFila() != 0 && precisa.getColumna() != 0;
    }

    private boolean tieneZonaValida(DeZona zona) {
        return zona.getZona() != null && !zona.getZona().isBlank();
    }
}

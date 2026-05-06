package edu.esi.ds.esientradas.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Precisa;
import edu.esi.ds.esientradas.model.DeZona;

/**
 * Servicio que transforma una Entrada en su ubicación formateada.
 * PROPÓSITO: Polimorfismo — tratar diferente a Precisa y DeZona sin lógica en el controller.
 */
@Service
public class UbicacionMapper {

    public Map<String, Object> mapearUbicacion(Entrada entrada) {
        if (entrada instanceof Precisa) {
            return mapearButaca((Precisa) entrada);
        } else if (entrada instanceof DeZona) {
            return mapearZona((DeZona) entrada);
        }
        return crearUbicacionGeneral();
    }

    private Map<String, Object> mapearButaca(Precisa precisa) {
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("tipo", "BUTACA");
        ubicacion.put("planta", precisa.getPlanta());
        ubicacion.put("fila", precisa.getFila());
        ubicacion.put("columna", precisa.getColumna());

        String descripcion = tieneCoordenadasButacaCompletas(precisa)
            ? String.format("Planta %d, Fila %d, Columna %d",
                precisa.getPlanta(), precisa.getFila(), precisa.getColumna())
            : "Ubicación de butaca pendiente de completar";
        ubicacion.put("descripcion", descripcion);
        return ubicacion;
    }

    private Map<String, Object> mapearZona(DeZona zona) {
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("tipo", "ZONA");
        ubicacion.put("zona", zona.getZona());

        String descripcion = tieneZonaValida(zona)
            ? String.format("Zona: %s", zona.getZona())
            : "Ubicación de zona pendiente de completar";
        ubicacion.put("descripcion", descripcion);
        return ubicacion;
    }

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

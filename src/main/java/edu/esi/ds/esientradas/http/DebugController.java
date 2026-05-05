package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.model.Entrada;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private EntradaDao entradaDao;

    @GetMapping("/entradas-info")
    public Map<String, Object> diagnosticarEntradas() {
        List<Entrada> todas = entradaDao.findAll();
        List<String> tipos = entradaDao.obtenerTiposEntrada();
        
        long butacas = todas.stream().filter(e -> e.getClass().getSimpleName().equals("Precisa")).count();
        long zonas = todas.stream().filter(e -> e.getClass().getSimpleName().equals("DeZona")).count();
        
        return Map.of(
            "total_entradas", todas.size(),
            "butacas", butacas,
            "zonas", zonas,
            "tipos_en_bd", tipos != null ? tipos : List.of("(vacío)"),
            "tipos_detectados", todas.stream()
                .map(e -> e.getClass().getSimpleName())
                .distinct()
                .collect(Collectors.toList())
        );
    }
}

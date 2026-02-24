package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import edu.esi.ds.esientradas.services.ReservasService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/reservas")
public class ReservasController {

    @Autowired
    private ReservasService service;

    @PutMapping("/reservar") // PUT PORQUE SE VA A MODIFICAR EL ESTADO DE LA ENTRADA EN LA BASE DE DATOS, Y SE VA A ACUMULAR EL PRECIO DE LA ENTRADA RESERVADA EN LA SESIÓN
    public Long reservar(HttpSession session, @RequestParam Long idEntrada) { // @RequestParam le dice a Spring que el valor del parámetro hay que sacarlo del ? de la url
        Long precioEntrada = this.service.reservar(idEntrada); // Punto de depuración aquí para probar con 1 entrada. Prueba en Bruno
        Long precioTotal = (Long) session.getAttribute("precioTotal"); // se obtiene el precio total de la sesión, que se ha ido acumulando con las reservas anteriores
                                                                            // Almacenar memoria pequeña, si no peto el servidor
        if (precioTotal == null) {  // si el precio total es null, se inicializa a 0
            precioTotal = precioEntrada; // si el precio total es null, se inicializa al precio de la entrada reservada
            session.setAttribute("precioTotal", precioTotal); // se almacena el precio total en la sesión, para que esté disponible en las siguientes reservas
        } else {
            precioTotal += precioEntrada; // si el precio total no es null, se acumula el precio de la entrada reservada al precio total
            session.setAttribute("precioTotal", precioTotal); // se actualiza el precio total en la sesión, para que esté disponible en las siguientes reservas
        }
        return precioTotal; // se devuelve el precio total acumulado de las reservas realizadas en la sesión
    }

}
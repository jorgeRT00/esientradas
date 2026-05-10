package edu.esi.ds.esientradas.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;


@Service
public class UsuariosService {

    private static final String INTERNAL_SECRET = "secreto-esi-interno-2025";
    private final RestTemplate rest = new RestTemplate();

    public String checkToken(String userToken) {
        
        String endpoint = "http://localhost:8081/external/checkToken/" + userToken;
        
        // OWASP A01 - Control de acceso:
        // Se envia la cabecera secreta para que esiusuarios sepa que la peticion
        // viene de esientradas y no de un cliente externo.
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Secret", INTERNAL_SECRET);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rest.exchange(endpoint, HttpMethod.GET, entity, String.class);
            String email = response.getBody();
            if (email == null || email.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
            }
            return email;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error al validar el token");
        }
    }
}

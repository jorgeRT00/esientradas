package edu.esi.ds.esientradas.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UsuariosService {
    
    public void checkToken(String userToken) {
        
        String endpoint = "http://localhost:8081/usuarios/checkToken?token=";
        RestTemplate rest = new RestTemplate();

        try {
            String username = rest.getForObject(endpoint + "/" + userToken, String.class);
            if (username == null || username.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
            }
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error al validar el token");
        }
    }
}

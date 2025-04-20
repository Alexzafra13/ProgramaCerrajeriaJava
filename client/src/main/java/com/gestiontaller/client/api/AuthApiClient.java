package com.gestiontaller.client.api;

import com.gestiontaller.common.model.auth.LoginRequest;
import com.gestiontaller.common.model.auth.LoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class AuthApiClient extends BaseApiClient {

    public AuthApiClient(String serverUrl) {
        super(serverUrl, "/api/auth");
    }

    public LoginResponse login(String username, String password) {
        try {
            LoginRequest request = new LoginRequest();
            request.setUsername(username);
            request.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

            logger.debug("Enviando solicitud de login para usuario: {}", username);
            LoginResponse response = restTemplate.postForObject(baseUrl + "/login", entity, LoginResponse.class);

            if (response != null && response.getToken() != null) {
                logger.info("Login exitoso para usuario: {}", username);
            } else {
                logger.warn("No se recibió token en la respuesta de login");
            }

            return response;
        } catch (Exception e) {
            logError("login", e);
            throw e;
        }
    }
}
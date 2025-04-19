package com.gestiontaller.client.api;

import com.gestiontaller.client.model.auth.LoginRequest;
import com.gestiontaller.client.model.auth.LoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class AuthApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AuthApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/auth";
        System.out.println("Inicializando AuthApiClient con URL base: " + this.baseUrl);
    }

    public LoginResponse login(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        System.out.println("Enviando solicitud de login a: " + baseUrl + "/login");
        LoginResponse response = restTemplate.postForObject(baseUrl + "/login", entity, LoginResponse.class);

        if (response != null && response.getToken() != null) {
            System.out.println("Login exitoso, token recibido: " + response.getToken().substring(0, Math.min(15, response.getToken().length())) + "...");
        } else {
            System.out.println("No se recibió token en la respuesta de login");
        }

        return response;
    }
}
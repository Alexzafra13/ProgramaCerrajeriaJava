package com.gestiontaller.client.api;

import com.gestiontaller.client.model.auth.LoginRequest;
import com.gestiontaller.client.model.auth.LoginResponse;
import org.springframework.web.client.RestTemplate;

public class AuthApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AuthApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/auth";
    }

    public LoginResponse login(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return restTemplate.postForObject(baseUrl + "/login", request, LoginResponse.class);
    }
}
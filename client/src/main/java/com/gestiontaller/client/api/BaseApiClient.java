package com.gestiontaller.client.api;

import com.gestiontaller.client.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Clase base para todos los clientes API que proporciona funcionalidad común
 */
public abstract class BaseApiClient {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    public BaseApiClient(String serverUrl, String apiPath) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + apiPath;
        logger.info("Inicializando API client con URL base: {}", this.baseUrl);
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        } else {
            logger.warn("No se encontró token de autenticación");
        }
        return headers;
    }

    protected <T> HttpEntity<T> createEntity(T body) {
        return new HttpEntity<>(body, createHeaders());
    }

    protected HttpEntity<?> createEntity() {
        return new HttpEntity<>(createHeaders());
    }

    protected void logError(String metodo, Exception e) {
        logger.error("Error en {}: {}", metodo, e.getMessage(), e);
    }
}
package com.gestiontaller.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gestiontaller.client.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase base para todos los clientes API que proporciona funcionalidad común
 */
public abstract class BaseApiClient {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    public BaseApiClient(String serverUrl, String apiPath) {
        // Crear y configurar RestTemplate con soporte para fechas Java 8
        this.restTemplate = createRestTemplateWithDateSupport();
        this.baseUrl = serverUrl + apiPath;
        logger.info("Inicializando API client con URL base: {}", this.baseUrl);
    }

    /**
     * Crea un RestTemplate configurado para manejar correctamente los tipos de fecha de Java 8
     */
    private RestTemplate createRestTemplateWithDateSupport() {
        RestTemplate template = new RestTemplate();

        // Configurar el ObjectMapper para manejar fechas de Java 8
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Crear un nuevo converter con el ObjectMapper configurado
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        // Añadir el nuevo converter
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);

        // Añadir el resto de converters del RestTemplate
        converters.addAll(template.getMessageConverters());

        // Establecer la lista de converters modificada
        template.setMessageConverters(converters);

        return template;
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
package com.gestiontaller.client.api;

import com.gestiontaller.client.model.serie.PerfilSerieDTO;
import com.gestiontaller.client.model.serie.SerieAluminioDTO;
import com.gestiontaller.client.util.SessionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SerieApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public SerieApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/series";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // Añadir token de autenticación si está disponible
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                baseUrl + "/aluminio",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
        );

        return response.getBody();
    }

    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<List<PerfilSerieDTO>> response = restTemplate.exchange(
                baseUrl + "/{serieId}/perfiles",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<PerfilSerieDTO>>() {},
                serieId
        );

        return response.getBody();
    }

    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        HttpEntity<PerfilSerieDTO> entity = new HttpEntity<>(perfilDTO, createHeaders());

        return restTemplate.postForObject(
                baseUrl + "/perfiles",
                entity,
                PerfilSerieDTO.class
        );
    }

    public void eliminarPerfilSerie(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        restTemplate.exchange(
                baseUrl + "/perfiles/{id}",
                HttpMethod.DELETE,
                entity,
                Void.class,
                id
        );
    }

    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        HttpEntity<SerieAluminioDTO> entity = new HttpEntity<>(serieDTO, createHeaders());

        return restTemplate.postForObject(
                baseUrl + "/aluminio",
                entity,
                SerieAluminioDTO.class
        );
    }
}
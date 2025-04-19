package com.gestiontaller.client.api;

import com.gestiontaller.client.model.serie.PerfilSerieDTO;
import com.gestiontaller.client.model.serie.SerieAluminioDTO;
import com.gestiontaller.client.util.SessionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerieApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public SerieApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/series";
        System.out.println("Inicializando SerieApiClient con URL base: " + this.baseUrl);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // Añadir token de autenticación si está disponible
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            // Usar formato Bearer
            headers.set("Authorization", "Bearer " + token);
            System.out.println("Enviando token con Bearer: Bearer " + token.substring(0, Math.min(15, token.length())) + "...");

            // También agrega formato básico de contenido
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        } else {
            System.out.println("ADVERTENCIA: Token de autenticación no disponible");
        }
        return headers;
    }

    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        try {
            System.out.println("Solicitando series de aluminio al servidor...");
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                    baseUrl + "/aluminio",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
            );

            List<SerieAluminioDTO> series = response.getBody();
            if (series != null) {
                System.out.println("Respuesta del servidor: " + series.size() + " series recibidas");
                for (SerieAluminioDTO serie : series) {
                    System.out.println("  - Serie: " + serie.getCodigo() + " - " + serie.getNombre());
                }
            } else {
                System.out.println("Respuesta del servidor: sin series (body null)");
            }
            return series != null ? series : new ArrayList<>();
        } catch (HttpClientErrorException.Unauthorized e) {
            System.err.println("Error de autenticación al obtener series. Token posiblemente expirado o inválido.");
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo series: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        try {
            System.out.println("Solicitando perfiles para serie ID: " + serieId);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<List<PerfilSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/{serieId}/perfiles",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<PerfilSerieDTO>>() {},
                    serieId
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo perfiles para serie ID " + serieId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        try {
            System.out.println("Guardando perfil: " + perfilDTO.getCodigo());
            HttpEntity<PerfilSerieDTO> entity = new HttpEntity<>(perfilDTO, createHeaders());

            return restTemplate.postForObject(
                    baseUrl + "/perfiles",
                    entity,
                    PerfilSerieDTO.class
            );
        } catch (Exception e) {
            System.err.println("Error guardando perfil: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarPerfilSerie(Long id) {
        try {
            System.out.println("Eliminando perfil ID: " + id);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            restTemplate.exchange(
                    baseUrl + "/perfiles/{id}",
                    HttpMethod.DELETE,
                    entity,
                    Void.class,
                    id
            );
        } catch (Exception e) {
            System.err.println("Error eliminando perfil ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        try {
            System.out.println("Guardando serie: " + serieDTO.getCodigo());
            HttpEntity<SerieAluminioDTO> entity = new HttpEntity<>(serieDTO, createHeaders());

            return restTemplate.postForObject(
                    baseUrl + "/aluminio",
                    entity,
                    SerieAluminioDTO.class
            );
        } catch (Exception e) {
            System.err.println("Error guardando serie: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarSerie(Long id) {
        try {
            System.out.println("Eliminando serie ID: " + id);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.DELETE,
                    entity,
                    Void.class,
                    id
            );
        } catch (Exception e) {
            System.err.println("Error eliminando serie ID " + id + ": " + e.getMessage());
            throw e;
        }
    }
}
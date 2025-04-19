package com.gestiontaller.client.api;

import com.gestiontaller.client.model.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.client.model.calculo.ResultadoCalculoDTO;
import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.util.SessionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSerieApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ConfiguracionSerieApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/configuraciones-serie";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", token);
        }
        return headers;
    }

    public List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<List<PlantillaConfiguracionSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/serie/{serieId}",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<PlantillaConfiguracionSerieDTO>>() {},
                    serieId
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo configuraciones para serie: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            return restTemplate.exchange(
                    baseUrl + "/serie/{serieId}/hojas/{numHojas}",
                    HttpMethod.GET,
                    entity,
                    PlantillaConfiguracionSerieDTO.class,
                    serieId,
                    numHojas
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error obteniendo configuración: " + e.getMessage());
            return null;
        }
    }

    public ResultadoCalculoDTO aplicarConfiguracion(
            Long configuracionId,
            Integer ancho,
            Integer alto,
            Boolean incluyePersiana,
            Integer alturaCajon,
            TipoCristal tipoCristal) {

        try {
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/{configuracionId}/aplicar")
                    .queryParam("ancho", ancho)
                    .queryParam("alto", alto)
                    .queryParam("incluyePersiana", incluyePersiana);

            if (alturaCajon != null) {
                builder.queryParam("alturaCajon", alturaCajon);
            }

            if (tipoCristal != null) {
                builder.queryParam("tipoCristal", tipoCristal);
            }

            return restTemplate.exchange(
                    builder.buildAndExpand(configuracionId).toUri(),
                    HttpMethod.POST,
                    entity,
                    ResultadoCalculoDTO.class
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error aplicando configuración: " + e.getMessage());
            return null;
        }
    }

    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
        try {
            HttpEntity<PlantillaConfiguracionSerieDTO> entity = new HttpEntity<>(dto, createHeaders());

            return restTemplate.postForObject(
                    baseUrl,
                    entity,
                    PlantillaConfiguracionSerieDTO.class
            );
        } catch (Exception e) {
            System.err.println("Error guardando configuración: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarConfiguracion(Long id) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.DELETE,
                    entity,
                    Void.class,
                    id
            );
        } catch (Exception e) {
            System.err.println("Error eliminando configuración: " + e.getMessage());
            throw e;
        }
    }
}
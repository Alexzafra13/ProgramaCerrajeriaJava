package com.gestiontaller.client.api;

import com.gestiontaller.client.model.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.client.model.calculo.ResultadoCalculoDTO;
import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.util.SessionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfiguracionSerieApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ConfiguracionSerieApiClient(String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = serverUrl + "/api/configuraciones-serie";
        System.out.println("Inicializando ConfiguracionSerieApiClient con URL base: " + this.baseUrl);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            // Añadir token de autenticación
            headers.set("Authorization", "Bearer " + token);
            System.out.println("Enviando token: Bearer " + token.substring(0, Math.min(15, token.length())) + "...");
        } else {
            System.out.println("ADVERTENCIA: No se encontró token de autenticación");
        }
        return headers;
    }

    public List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId) {
        try {
            System.out.println("Solicitando configuraciones para serie ID: " + serieId);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            String url = baseUrl + "/serie/" + serieId;
            System.out.println("URL: " + url);

            ResponseEntity<List<PlantillaConfiguracionSerieDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<PlantillaConfiguracionSerieDTO>>() {}
            );

            List<PlantillaConfiguracionSerieDTO> configs = response.getBody();
            if (configs != null) {
                System.out.println("Recibidas " + configs.size() + " configuraciones");
                return configs;
            } else {
                System.out.println("La respuesta no contiene configuraciones (body null)");
                return new ArrayList<>();
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error de servidor (" + e.getStatusCode() + "): " + e.getMessage());
            System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error obteniendo configuraciones para serie: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        try {
            System.out.println("Solicitando configuración para serie ID: " + serieId + " con " + numHojas + " hojas");
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            String url = baseUrl + "/serie/" + serieId + "/hojas/" + numHojas;
            System.out.println("URL: " + url);

            ResponseEntity<PlantillaConfiguracionSerieDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlantillaConfiguracionSerieDTO.class
            );

            PlantillaConfiguracionSerieDTO config = response.getBody();
            if (config != null) {
                System.out.println("Configuración recibida: " + config.getNombre());
            } else {
                System.out.println("La respuesta no contiene configuración (body null)");
            }
            return config;
        } catch (HttpClientErrorException e) {
            System.err.println("Error de servidor (" + e.getStatusCode() + "): " + e.getMessage());
            System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("Error obteniendo configuración: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Aplicando configuración ID: " + configuracionId);

            // Construir URL con parámetros
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + configuracionId + "/aplicar")
                    .queryParam("ancho", ancho)
                    .queryParam("alto", alto)
                    .queryParam("incluyePersiana", incluyePersiana);

            if (alturaCajon != null) {
                builder.queryParam("alturaCajon", alturaCajon);
            }

            if (tipoCristal != null) {
                builder.queryParam("tipoCristal", tipoCristal);
            }

            String url = builder.toUriString();
            System.out.println("URL completa: " + url);

            // Crear cabeceras
            HttpHeaders headers = createHeaders();
            System.out.println("Cabeceras: " + headers);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // Realizar petición GET en lugar de POST
            ResponseEntity<ResultadoCalculoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ResultadoCalculoDTO.class
            );

            // Procesar respuesta
            ResultadoCalculoDTO resultado = response.getBody();
            if (resultado != null) {
                System.out.println("Cálculo completado exitosamente");
                if (resultado.getCortes() != null) {
                    System.out.println("Número de cortes: " + resultado.getCortes().size());
                }
            } else {
                System.out.println("La respuesta no contiene resultados (body null)");
            }
            return resultado;
        } catch (HttpClientErrorException e) {
            System.err.println("Error de servidor (" + e.getStatusCode() + "): " + e.getMessage());
            System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Error aplicando configuración: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
        try {
            System.out.println("Guardando configuración: " + dto.getNombre());
            HttpEntity<PlantillaConfiguracionSerieDTO> entity = new HttpEntity<>(dto, createHeaders());

            ResponseEntity<PlantillaConfiguracionSerieDTO> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    entity,
                    PlantillaConfiguracionSerieDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error guardando configuración: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void eliminarConfiguracion(Long id) {
        try {
            System.out.println("Eliminando configuración ID: " + id);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());

            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            System.out.println("Configuración eliminada correctamente");
        } catch (Exception e) {
            System.err.println("Error eliminando configuración: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
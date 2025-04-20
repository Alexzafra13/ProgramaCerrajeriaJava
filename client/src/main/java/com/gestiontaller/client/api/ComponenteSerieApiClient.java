package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente API para acceder a los servicios de componentes de series
 */
public class ComponenteSerieApiClient extends BaseApiClient {

    public ComponenteSerieApiClient(String serverUrl) {
        super(serverUrl, "/api/componentes-serie");
    }

    /**
     * Obtiene todos los componentes base para una serie
     */
    public List<MaterialBaseSerieDTO> obtenerComponentesPorSerie(Long serieId) {
        try {
            logger.debug("Solicitando componentes para serie ID: {}", serieId);
            ResponseEntity<List<MaterialBaseSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/serie/" + serieId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<MaterialBaseSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerComponentesPorSerie", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene componentes de un tipo específico para una serie
     */
    public List<MaterialBaseSerieDTO> obtenerComponentesPorTipo(Long serieId, String tipoMaterial) {
        try {
            logger.debug("Solicitando componentes de tipo {} para serie ID: {}", tipoMaterial, serieId);
            ResponseEntity<List<MaterialBaseSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/serie/" + serieId + "/tipo/" + tipoMaterial,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<MaterialBaseSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerComponentesPorTipo", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un componente base para una serie
     */
    public MaterialBaseSerieDTO guardarComponente(MaterialBaseSerieDTO componenteDTO) {
        try {
            logger.debug("Guardando componente: {} para serie ID: {}",
                    componenteDTO.getDescripcion(), componenteDTO.getSerieId());

            return restTemplate.postForObject(
                    baseUrl,
                    createEntity(componenteDTO),
                    MaterialBaseSerieDTO.class
            );
        } catch (Exception e) {
            logError("guardarComponente", e);
            throw e;
        }
    }

    /**
     * Elimina un componente base
     */
    public void eliminarComponente(Long id) {
        try {
            logger.debug("Eliminando componente ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarComponente", e);
            throw e;
        }
    }

    /**
     * Calcula los componentes necesarios para una ventana
     */
    public Map<String, List<MaterialBaseSerieDTO>> calcularComponentesVentana(
            Long serieId, Integer numHojas, Integer ancho, Integer alto, Boolean incluyePersiana) {
        try {
            logger.debug("Calculando componentes para ventana: Serie ID: {}, {} hojas, {}x{} mm",
                    serieId, numHojas, ancho, alto);

            String url = String.format("%s/calcular?serieId=%d&numHojas=%d&ancho=%d&alto=%d&incluyePersiana=%b",
                    baseUrl, serieId, numHojas, ancho, alto, incluyePersiana);

            ResponseEntity<Map<String, List<MaterialBaseSerieDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<Map<String, List<MaterialBaseSerieDTO>>>() {}
            );

            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (Exception e) {
            logError("calcularComponentesVentana", e);
            return new HashMap<>();
        }
    }

    /**
     * Genera una configuración estándar de componentes para una serie
     */
    public void generarConfiguracionEstandar(Long serieId, String tipoSerie) {
        try {
            logger.debug("Generando configuración estándar para serie ID: {} de tipo {}", serieId, tipoSerie);

            String url = String.format("%s/generar-configuracion-estandar?serieId=%d&tipoSerie=%s",
                    baseUrl, serieId, tipoSerie);

            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("generarConfiguracionEstandar", e);
            throw e;
        }
    }
}
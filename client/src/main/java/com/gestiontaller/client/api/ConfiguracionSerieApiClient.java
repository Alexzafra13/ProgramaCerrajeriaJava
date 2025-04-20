package com.gestiontaller.client.api;

import com.gestiontaller.client.model.calculo.ResultadoCalculoDTO;
import com.gestiontaller.client.model.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.common.model.ventana.TipoCristal;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSerieApiClient extends BaseApiClient {

    public ConfiguracionSerieApiClient(String serverUrl) {
        super(serverUrl, "/api/configuraciones-serie");
    }

    public List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId) {
        try {
            logger.debug("Solicitando configuraciones para serie ID: {}", serieId);
            String url = baseUrl + "/serie/" + serieId;
            logger.trace("URL: {}", url);

            ResponseEntity<List<PlantillaConfiguracionSerieDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PlantillaConfiguracionSerieDTO>>() {}
            );

            List<PlantillaConfiguracionSerieDTO> configs = response.getBody();
            if (configs != null) {
                logger.debug("Recibidas {} configuraciones", configs.size());
                return configs;
            } else {
                logger.warn("La respuesta no contiene configuraciones (body null)");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logError("obtenerConfiguracionesPorSerie", e);
            return new ArrayList<>();
        }
    }

    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        try {
            logger.debug("Solicitando configuración para serie ID: {} con {} hojas", serieId, numHojas);
            String url = baseUrl + "/serie/" + serieId + "/hojas/" + numHojas;
            logger.trace("URL: {}", url);

            ResponseEntity<PlantillaConfiguracionSerieDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    PlantillaConfiguracionSerieDTO.class
            );

            PlantillaConfiguracionSerieDTO config = response.getBody();
            if (config != null) {
                logger.debug("Configuración recibida: {}", config.getNombre());
            } else {
                logger.warn("La respuesta no contiene configuración (body null)");
            }
            return config;
        } catch (Exception e) {
            logError("obtenerConfiguracionPorSerieYHojas", e);
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
            logger.debug("Aplicando configuración ID: {}", configuracionId);

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
            logger.trace("URL: {}", url);

            ResponseEntity<ResultadoCalculoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    createEntity(),
                    ResultadoCalculoDTO.class
            );

            ResultadoCalculoDTO resultado = response.getBody();
            if (resultado != null) {
                logger.debug("Cálculo completado correctamente");
            } else {
                logger.warn("La respuesta no contiene resultados (body null)");
            }
            return resultado;
        } catch (Exception e) {
            logError("aplicarConfiguracion", e);
            return null;
        }
    }

    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
        try {
            logger.debug("Guardando configuración: {}", dto.getNombre());
            return restTemplate.postForObject(
                    baseUrl,
                    createEntity(dto),
                    PlantillaConfiguracionSerieDTO.class
            );
        } catch (Exception e) {
            logError("guardarConfiguracion", e);
            throw e;
        }
    }

    public void eliminarConfiguracion(Long id) {
        try {
            logger.debug("Eliminando configuración ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
            logger.debug("Configuración eliminada correctamente");
        } catch (Exception e) {
            logError("eliminarConfiguracion", e);
            throw e;
        }
    }
}
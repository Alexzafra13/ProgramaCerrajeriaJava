package com.gestiontaller.client.api;

import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.model.calculo.ResultadoCalculoDTO;
import com.gestiontaller.client.model.configuracion.PlantillaConfiguracionSerieDTO;
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
            System.out.println("Solicitando configuraciones para serie ID: " + serieId);
            String url = baseUrl + "/serie/" + serieId;
            System.out.println("URL: " + url);

            ResponseEntity<List<PlantillaConfiguracionSerieDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
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
        } catch (Exception e) {
            logError("obtenerConfiguracionesPorSerie", e);
            return new ArrayList<>();
        }
    }

    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        try {
            System.out.println("Solicitando configuración para serie ID: " + serieId + " con " + numHojas + " hojas");
            String url = baseUrl + "/serie/" + serieId + "/hojas/" + numHojas;
            System.out.println("URL: " + url);

            ResponseEntity<PlantillaConfiguracionSerieDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    PlantillaConfiguracionSerieDTO.class
            );

            PlantillaConfiguracionSerieDTO config = response.getBody();
            if (config != null) {
                System.out.println("Configuración recibida: " + config.getNombre());
            } else {
                System.out.println("La respuesta no contiene configuración (body null)");
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
            System.out.println("Aplicando configuración ID: " + configuracionId);

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
            System.out.println("URL: " + url);

            ResponseEntity<ResultadoCalculoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    createEntity(),
                    ResultadoCalculoDTO.class
            );

            ResultadoCalculoDTO resultado = response.getBody();
            if (resultado != null) {
                System.out.println("Cálculo completado correctamente");
            } else {
                System.out.println("La respuesta no contiene resultados (body null)");
            }
            return resultado;
        } catch (Exception e) {
            logError("aplicarConfiguracion", e);
            return null;
        }
    }

    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
        try {
            System.out.println("Guardando configuración: " + dto.getNombre());
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
            System.out.println("Eliminando configuración ID: " + id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
            System.out.println("Configuración eliminada correctamente");
        } catch (Exception e) {
            logError("eliminarConfiguracion", e);
            throw e;
        }
    }
}
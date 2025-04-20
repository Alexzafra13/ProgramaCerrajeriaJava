package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.serie.PerfilSerieDTO;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerieApiClient extends BaseApiClient {

    public SerieApiClient(String serverUrl) {
        super(serverUrl, "/api/series");
    }

    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        try {
            logger.debug("Solicitando series de aluminio al servidor...");
            ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                    baseUrl + "/aluminio",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
            );

            List<SerieAluminioDTO> series = response.getBody();
            if (series != null) {
                logger.debug("Respuesta del servidor: {} series recibidas", series.size());
            } else {
                logger.warn("Respuesta del servidor: sin series (body null)");
            }
            return series != null ? series : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerSeriesAluminio", e);
            return new ArrayList<>();
        }
    }

    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        try {
            logger.debug("Solicitando perfiles para serie ID: {}", serieId);
            ResponseEntity<List<PerfilSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/{serieId}/perfiles",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PerfilSerieDTO>>() {},
                    serieId
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPerfilesPorSerieId", e);
            return new ArrayList<>();
        }
    }

    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        try {
            logger.debug("Guardando perfil: {}", perfilDTO.getCodigo());
            return restTemplate.postForObject(
                    baseUrl + "/perfiles",
                    createEntity(perfilDTO),
                    PerfilSerieDTO.class
            );
        } catch (Exception e) {
            logError("guardarPerfilSerie", e);
            throw e;
        }
    }

    public void eliminarPerfilSerie(Long id) {
        try {
            logger.debug("Eliminando perfil ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/perfiles/{id}",
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class,
                    id
            );
        } catch (Exception e) {
            logError("eliminarPerfilSerie", e);
            throw e;
        }
    }

    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        try {
            logger.debug("Guardando serie: {}", serieDTO.getCodigo());
            return restTemplate.postForObject(
                    baseUrl + "/aluminio",
                    createEntity(serieDTO),
                    SerieAluminioDTO.class
            );
        } catch (Exception e) {
            logError("guardarSerieAluminio", e);
            throw e;
        }
    }

    public SerieAluminioDTO crearSerieCompleta(String codigo, String nombre, String descripcion,
                                               String tipoSerie, boolean roturaPuente, boolean permitePersiana) {
        try {
            logger.debug("Creando serie completa: {}", codigo);

            Map<String, Object> datos = new HashMap<>();
            datos.put("codigo", codigo);
            datos.put("nombre", nombre);
            datos.put("descripcion", descripcion);
            datos.put("tipoSerie", tipoSerie);
            datos.put("roturaPuente", roturaPuente);
            datos.put("permitePersiana", permitePersiana);

            return restTemplate.postForObject(
                    baseUrl + "/aluminio/completa",
                    createEntity(datos),
                    SerieAluminioDTO.class
            );
        } catch (Exception e) {
            logError("crearSerieCompleta", e);
            throw e;
        }
    }

    public void eliminarSerie(Long id) {
        try {
            logger.debug("Eliminando serie ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/{id}",
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class,
                    id
            );
        } catch (Exception e) {
            logError("eliminarSerie", e);
            throw e;
        }
    }
}
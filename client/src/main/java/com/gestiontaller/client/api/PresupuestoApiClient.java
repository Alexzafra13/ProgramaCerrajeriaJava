package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente API para acceder a los servicios de presupuestos
 */
public class PresupuestoApiClient extends BaseApiClient {

    public PresupuestoApiClient(String serverUrl) {
        super(serverUrl, "/api/presupuestos");
    }

    /**
     * Obtiene todos los presupuestos
     */
    public List<PresupuestoDTO> obtenerTodos() {
        try {
            logger.debug("Solicitando todos los presupuestos");
            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerTodos", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un presupuesto por su ID
     */
    public PresupuestoDTO obtenerPorId(Long id) {
        try {
            logger.debug("Solicitando presupuesto con ID: {}", id);
            ResponseEntity<PresupuestoDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    PresupuestoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorId", e);
            throw e;
        }
    }

    /**
     * Obtiene un presupuesto por su número
     */
    public PresupuestoDTO obtenerPorNumero(String numero) {
        try {
            logger.debug("Solicitando presupuesto con número: {}", numero);
            ResponseEntity<PresupuestoDTO> response = restTemplate.exchange(
                    baseUrl + "/numero/" + numero,
                    HttpMethod.GET,
                    createEntity(),
                    PresupuestoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorNumero", e);
            throw e;
        }
    }

    /**
     * Obtiene presupuestos por cliente
     */
    public List<PresupuestoDTO> obtenerPorCliente(Long clienteId) {
        try {
            logger.debug("Solicitando presupuestos por cliente ID: {}", clienteId);
            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    baseUrl + "/cliente/" + clienteId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorCliente", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene presupuestos por estado
     */
    public List<PresupuestoDTO> obtenerPorEstado(EstadoPresupuesto estado) {
        try {
            logger.debug("Solicitando presupuestos por estado: {}", estado);
            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    baseUrl + "/estado/" + estado,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorEstado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene presupuestos entre fechas
     */
    public List<PresupuestoDTO> obtenerPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            logger.debug("Solicitando presupuestos entre fechas: {} y {}", fechaInicio, fechaFin);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/fechas")
                    .queryParam("fechaInicio", fechaInicio.toString())
                    .queryParam("fechaFin", fechaFin.toString());

            String url = builder.toUriString();

            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorFechas", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca presupuestos por texto
     */
    public List<PresupuestoDTO> buscar(String texto) {
        try {
            logger.debug("Buscando presupuestos con texto: {}", texto);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar")
                    .queryParam("texto", texto);

            String url = builder.toUriString();

            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscar", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca presupuestos por texto y estado
     */
    public List<PresupuestoDTO> buscarPorEstado(String texto, EstadoPresupuesto estado) {
        try {
            logger.debug("Buscando presupuestos con texto: {} y estado: {}", texto, estado);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar/estado")
                    .queryParam("texto", texto)
                    .queryParam("estado", estado);

            String url = builder.toUriString();

            ResponseEntity<List<PresupuestoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PresupuestoDTO>>() {
                    }
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscarPorEstado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un presupuesto (crea o actualiza)
     */
    public PresupuestoDTO guardar(PresupuestoDTO presupuestoDTO) {
        try {
            logger.debug("Guardando presupuesto: {}", presupuestoDTO.getNumero());

            if (presupuestoDTO.getId() != null) {
                // Actualización
                ResponseEntity<PresupuestoDTO> response = restTemplate.exchange(
                        baseUrl + "/" + presupuestoDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(presupuestoDTO),
                        PresupuestoDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl,
                        createEntity(presupuestoDTO),
                        PresupuestoDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardar", e);
            throw e;
        }
    }

    /**
     * Actualiza el estado de un presupuesto
     */
    public PresupuestoDTO actualizarEstado(Long id, EstadoPresupuesto nuevoEstado, String motivoRechazo) {
        try {
            logger.debug("Actualizando estado de presupuesto ID: {} a {}", id, nuevoEstado);

            Map<String, Object> datos = new HashMap<>();
            datos.put("estado", nuevoEstado.toString());
            if (motivoRechazo != null) {
                datos.put("motivoRechazo", motivoRechazo);
            }

            ResponseEntity<PresupuestoDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id + "/estado",
                    HttpMethod.PUT,
                    createEntity(datos),
                    PresupuestoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("actualizarEstado", e);
            throw e;
        }
    }

    /**
     * Elimina un presupuesto
     */
    public void eliminar(Long id) {
        try {
            logger.debug("Eliminando presupuesto con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminar", e);
            throw e;
        }
    }

    /**
     * Agrega una línea a un presupuesto
     */
    public LineaPresupuestoDTO agregarLinea(Long presupuestoId, LineaPresupuestoDTO lineaDTO) {
        try {
            logger.debug("Agregando línea a presupuesto ID: {}", presupuestoId);
            return restTemplate.postForObject(
                    baseUrl + "/" + presupuestoId + "/lineas",
                    createEntity(lineaDTO),
                    LineaPresupuestoDTO.class
            );
        } catch (Exception e) {
            logError("agregarLinea", e);
            throw e;
        }
    }

    /**
     * Actualiza una línea de presupuesto
     */
    public LineaPresupuestoDTO actualizarLinea(LineaPresupuestoDTO lineaDTO) {
        try {
            logger.debug("Actualizando línea de presupuesto ID: {}", lineaDTO.getId());
            ResponseEntity<LineaPresupuestoDTO> response = restTemplate.exchange(
                    baseUrl + "/lineas/" + lineaDTO.getId(),
                    HttpMethod.PUT,
                    createEntity(lineaDTO),
                    LineaPresupuestoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("actualizarLinea", e);
            throw e;
        }
    }

    /**
     * Elimina una línea de presupuesto
     */
    public void eliminarLinea(Long lineaId) {
        try {
            logger.debug("Eliminando línea de presupuesto ID: {}", lineaId);
            restTemplate.exchange(
                    baseUrl + "/lineas/" + lineaId,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarLinea", e);
            throw e;
        }
    }

    /**
     * Recalcula los totales de un presupuesto
     */
    public PresupuestoDTO recalcularTotales(Long presupuestoId) {
        try {
            logger.debug("Recalculando totales de presupuesto ID: {}", presupuestoId);
            return restTemplate.postForObject(
                    baseUrl + "/" + presupuestoId + "/recalcular",
                    createEntity(),
                    PresupuestoDTO.class
            );
        } catch (Exception e) {
            logError("recalcularTotales", e);
            throw e;
        }
    }

    /**
     * Genera un número para un presupuesto nuevo
     */
    public String generarNumeroPresupuesto() {
        try {
            logger.debug("Generando número de presupuesto");
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/generar-numero",
                    HttpMethod.GET,
                    createEntity(),
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("generarNumeroPresupuesto", e);
            throw e;
        }
    }
}
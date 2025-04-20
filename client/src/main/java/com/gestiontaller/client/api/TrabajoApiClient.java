package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.trabajo.CambioEstadoDTO;
import com.gestiontaller.common.dto.trabajo.MaterialAsignadoDTO;
import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente API para acceder a los servicios de trabajos
 */
public class TrabajoApiClient extends BaseApiClient {

    public TrabajoApiClient(String serverUrl) {
        super(serverUrl, "/api/trabajos");
    }

    /**
     * Obtiene todos los trabajos
     */
    public List<TrabajoDTO> obtenerTodos() {
        try {
            logger.debug("Solicitando todos los trabajos");
            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerTodos", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un trabajo por su ID
     */
    public TrabajoDTO obtenerPorId(Long id) {
        try {
            logger.debug("Solicitando trabajo con ID: {}", id);
            ResponseEntity<TrabajoDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    TrabajoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorId", e);
            throw e;
        }
    }

    /**
     * Obtiene un trabajo por su código
     */
    public TrabajoDTO obtenerPorCodigo(String codigo) {
        try {
            logger.debug("Solicitando trabajo con código: {}", codigo);
            ResponseEntity<TrabajoDTO> response = restTemplate.exchange(
                    baseUrl + "/codigo/" + codigo,
                    HttpMethod.GET,
                    createEntity(),
                    TrabajoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorCodigo", e);
            throw e;
        }
    }

    /**
     * Obtiene trabajos por cliente
     */
    public List<TrabajoDTO> obtenerPorCliente(Long clienteId) {
        try {
            logger.debug("Solicitando trabajos por cliente ID: {}", clienteId);
            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    baseUrl + "/cliente/" + clienteId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorCliente", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene trabajos por presupuesto
     */
    public List<TrabajoDTO> obtenerPorPresupuesto(Long presupuestoId) {
        try {
            logger.debug("Solicitando trabajos por presupuesto ID: {}", presupuestoId);
            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    baseUrl + "/presupuesto/" + presupuestoId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorPresupuesto", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene trabajos por estado
     */
    public List<TrabajoDTO> obtenerPorEstado(Long estadoId) {
        try {
            logger.debug("Solicitando trabajos por estado ID: {}", estadoId);
            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    baseUrl + "/estado/" + estadoId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorEstado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene trabajos por usuario asignado
     */
    public List<TrabajoDTO> obtenerPorUsuarioAsignado(Long usuarioId) {
        try {
            logger.debug("Solicitando trabajos por usuario asignado ID: {}", usuarioId);
            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    baseUrl + "/usuario/" + usuarioId,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorUsuarioAsignado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca trabajos por texto
     */
    public List<TrabajoDTO> buscar(String texto) {
        try {
            logger.debug("Buscando trabajos con texto: {}", texto);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar")
                    .queryParam("texto", texto);

            String url = builder.toUriString();

            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscar", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca trabajos por texto y estado
     */
    public List<TrabajoDTO> buscarPorEstado(String texto, Long estadoId) {
        try {
            logger.debug("Buscando trabajos con texto: {} y estado ID: {}", texto, estadoId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar/estado")
                    .queryParam("texto", texto)
                    .queryParam("estadoId", estadoId);

            String url = builder.toUriString();

            ResponseEntity<List<TrabajoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<TrabajoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscarPorEstado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un trabajo (crea o actualiza)
     */
    public TrabajoDTO guardar(TrabajoDTO trabajoDTO) {
        try {
            logger.debug("Guardando trabajo: {}", trabajoDTO.getCodigo());

            if (trabajoDTO.getId() != null) {
                // Actualización
                ResponseEntity<TrabajoDTO> response = restTemplate.exchange(
                        baseUrl + "/" + trabajoDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(trabajoDTO),
                        TrabajoDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl,
                        createEntity(trabajoDTO),
                        TrabajoDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardar", e);
            throw e;
        }
    }

    /**
     * Crea un trabajo a partir de un presupuesto
     */
    public TrabajoDTO crearDesdePrespuesto(Long presupuestoId) {
        try {
            logger.debug("Creando trabajo desde presupuesto ID: {}", presupuestoId);
            return restTemplate.postForObject(
                    baseUrl + "/desde-presupuesto/" + presupuestoId,
                    createEntity(),
                    TrabajoDTO.class
            );
        } catch (Exception e) {
            logError("crearDesdePrespuesto", e);
            throw e;
        }
    }

    /**
     * Cambia el estado de un trabajo
     */
    public TrabajoDTO cambiarEstado(Long id, Long estadoId, String observaciones, String motivoCambio, Long usuarioId) {
        try {
            logger.debug("Cambiando estado de trabajo ID: {} a estado ID: {}", id, estadoId);

            Map<String, Object> datos = new HashMap<>();
            datos.put("estadoId", estadoId);
            datos.put("observaciones", observaciones);
            datos.put("motivoCambio", motivoCambio);
            datos.put("usuarioId", usuarioId);

            return restTemplate.postForObject(
                    baseUrl + "/" + id + "/cambiar-estado",
                    createEntity(datos),
                    TrabajoDTO.class
            );
        } catch (Exception e) {
            logError("cambiarEstado", e);
            throw e;
        }
    }

    /**
     * Asigna un usuario a un trabajo
     */
    public TrabajoDTO asignarUsuario(Long id, Long usuarioId) {
        try {
            logger.debug("Asignando usuario ID: {} a trabajo ID: {}", usuarioId, id);

            Map<String, Object> datos = new HashMap<>();
            datos.put("usuarioId", usuarioId);

            return restTemplate.postForObject(
                    baseUrl + "/" + id + "/asignar-usuario",
                    createEntity(datos),
                    TrabajoDTO.class
            );
        } catch (Exception e) {
            logError("asignarUsuario", e);
            throw e;
        }
    }

    /**
     * Actualiza la prioridad de un trabajo
     */
    public TrabajoDTO actualizarPrioridad(Long id, PrioridadTrabajo prioridad) {
        try {
            logger.debug("Actualizando prioridad de trabajo ID: {} a {}", id, prioridad);

            Map<String, Object> datos = new HashMap<>();
            datos.put("prioridad", prioridad.toString());

            return restTemplate.postForObject(
                    baseUrl + "/" + id + "/actualizar-prioridad",
                    createEntity(datos),
                    TrabajoDTO.class
            );
        } catch (Exception e) {
            logError("actualizarPrioridad", e);
            throw e;
        }
    }

    /**
     * Obtiene el historial de cambios de estado de un trabajo
     */
    public List<CambioEstadoDTO> obtenerHistorialCambios(Long trabajoId) {
        try {
            logger.debug("Obteniendo historial de cambios para trabajo ID: {}", trabajoId);
            ResponseEntity<List<CambioEstadoDTO>> response = restTemplate.exchange(
                    baseUrl + "/" + trabajoId + "/historial",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<CambioEstadoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerHistorialCambios", e);
            return new ArrayList<>();
        }
    }

    /**
     * Añade un material a un trabajo
     */
    public MaterialAsignadoDTO agregarMaterial(Long trabajoId, MaterialAsignadoDTO materialDTO) {
        try {
            logger.debug("Agregando material a trabajo ID: {}", trabajoId);
            return restTemplate.postForObject(
                    baseUrl + "/" + trabajoId + "/materiales",
                    createEntity(materialDTO),
                    MaterialAsignadoDTO.class
            );
        } catch (Exception e) {
            logError("agregarMaterial", e);
            throw e;
        }
    }

    /**
     * Actualiza un material asignado
     */
    public MaterialAsignadoDTO actualizarMaterial(MaterialAsignadoDTO materialDTO) {
        try {
            logger.debug("Actualizando material ID: {}", materialDTO.getId());
            ResponseEntity<MaterialAsignadoDTO> response = restTemplate.exchange(
                    baseUrl + "/materiales/" + materialDTO.getId(),
                    HttpMethod.PUT,
                    createEntity(materialDTO),
                    MaterialAsignadoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("actualizarMaterial", e);
            throw e;
        }
    }

    /**
     * Elimina un material asignado
     */
    public void eliminarMaterial(Long materialId) {
        try {
            logger.debug("Eliminando material ID: {}", materialId);
            restTemplate.exchange(
                    baseUrl + "/materiales/" + materialId,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarMaterial", e);
            throw e;
        }
    }

    /**
     * Obtiene los materiales asignados a un trabajo
     */
    public List<MaterialAsignadoDTO> obtenerMaterialesAsignados(Long trabajoId) {
        try {
            logger.debug("Obteniendo materiales para trabajo ID: {}", trabajoId);
            ResponseEntity<List<MaterialAsignadoDTO>> response = restTemplate.exchange(
                    baseUrl + "/" + trabajoId + "/materiales",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<MaterialAsignadoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerMaterialesAsignados", e);
            return new ArrayList<>();
        }
    }

    /**
     * Genera un código para un trabajo nuevo
     */
    public String generarCodigoTrabajo() {
        try {
            logger.debug("Generando código de trabajo");
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/generar-codigo",
                    HttpMethod.GET,
                    createEntity(),
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("generarCodigoTrabajo", e);
            throw e;
        }
    }
}
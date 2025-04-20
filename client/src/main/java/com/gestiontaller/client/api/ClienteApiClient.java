package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente API para acceder a los servicios de clientes
 */
public class ClienteApiClient extends BaseApiClient {

    public ClienteApiClient(String serverUrl) {
        super(serverUrl, "/api/clientes");
    }

    /**
     * Obtiene todos los clientes
     */
    public List<ClienteDTO> obtenerTodos() {
        try {
            logger.debug("Solicitando todos los clientes");
            ResponseEntity<List<ClienteDTO>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<ClienteDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerTodos", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un cliente por su ID
     */
    public ClienteDTO obtenerPorId(Long id) {
        try {
            logger.debug("Solicitando cliente con ID: {}", id);
            ResponseEntity<ClienteDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    ClienteDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorId", e);
            throw e;
        }
    }

    /**
     * Obtiene un cliente por su código
     */
    public ClienteDTO obtenerPorCodigo(String codigo) {
        try {
            logger.debug("Solicitando cliente con código: {}", codigo);
            ResponseEntity<ClienteDTO> response = restTemplate.exchange(
                    baseUrl + "/codigo/" + codigo,
                    HttpMethod.GET,
                    createEntity(),
                    ClienteDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerPorCodigo", e);
            throw e;
        }
    }

    /**
     * Obtiene clientes por tipo
     */
    public List<ClienteDTO> obtenerPorTipo(TipoCliente tipo) {
        try {
            logger.debug("Solicitando clientes por tipo: {}", tipo);
            ResponseEntity<List<ClienteDTO>> response = restTemplate.exchange(
                    baseUrl + "/tipo/" + tipo,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<ClienteDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPorTipo", e);
            return new ArrayList<>();
        }
    }

    /**
     * Busca clientes por texto
     */
    public List<ClienteDTO> buscar(String texto) {
        try {
            logger.debug("Buscando clientes con texto: {}", texto);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar")
                    .queryParam("texto", texto);

            String url = builder.toUriString();

            ResponseEntity<List<ClienteDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<ClienteDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscar", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un cliente (crea o actualiza)
     */
    public ClienteDTO guardar(ClienteDTO clienteDTO) {
        try {
            logger.debug("Guardando cliente: {}", clienteDTO.getCodigo());

            if (clienteDTO.getId() != null) {
                // Actualización
                ResponseEntity<ClienteDTO> response = restTemplate.exchange(
                        baseUrl + "/" + clienteDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(clienteDTO),
                        ClienteDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl,
                        createEntity(clienteDTO),
                        ClienteDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardar", e);
            throw e;
        }
    }

    /**
     * Elimina un cliente
     */
    public void eliminar(Long id) {
        try {
            logger.debug("Eliminando cliente con ID: {}", id);
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
     * Genera un código para un cliente nuevo
     */
    public String generarCodigo(TipoCliente tipo) {
        try {
            logger.debug("Generando código para cliente de tipo: {}", tipo);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/generar-codigo")
                    .queryParam("tipo", tipo);

            String url = builder.toUriString();

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("generarCodigo", e);
            throw e;
        }
    }
}
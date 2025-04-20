package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.producto.ProductoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente API para acceder a los servicios de productos
 */
public class ProductoApiClient extends BaseApiClient {

    public ProductoApiClient(String serverUrl) {
        super(serverUrl, "/api/productos");
    }

    /**
     * Obtiene un producto por su ID
     */
    public ProductoDTO obtenerProductoPorId(Long id) {
        try {
            logger.debug("Solicitando producto con ID: {}", id);
            ResponseEntity<ProductoDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    ProductoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerProductoPorId", e);
            throw e;
        }
    }

    /**
     * Busca productos según filtros
     */
    public List<ProductoDTO> buscarProductos(String textoBusqueda, String categoria) {
        try {
            logger.debug("Buscando productos. Texto: {}, Categoría: {}", textoBusqueda, categoria);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/buscar");

            if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                builder.queryParam("texto", textoBusqueda);
            }

            if (categoria != null && !categoria.isEmpty()) {
                builder.queryParam("categoria", categoria);
            }

            String url = builder.toUriString();

            ResponseEntity<List<ProductoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<ProductoDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscarProductos", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un producto (nuevo o actualización)
     */
    public ProductoDTO guardarProducto(ProductoDTO productoDTO) {
        try {
            logger.debug("Guardando producto: {}", productoDTO.getCodigo());
            return restTemplate.postForObject(
                    baseUrl,
                    createEntity(productoDTO),
                    ProductoDTO.class
            );
        } catch (Exception e) {
            logError("guardarProducto", e);
            throw e;
        }
    }

    /**
     * Elimina un producto por su ID
     */
    public void eliminarProducto(Long id) {
        try {
            logger.debug("Eliminando producto con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarProducto", e);
            throw e;
        }
    }

    /**
     * Actualiza el stock de un producto
     */
    public ProductoDTO actualizarStock(Long id, Integer cantidad) {
        try {
            logger.debug("Actualizando stock para producto ID: {}, Cantidad: {}", id, cantidad);

            return restTemplate.exchange(
                    baseUrl + "/" + id + "/stock/" + cantidad,
                    HttpMethod.PUT,
                    createEntity(),
                    ProductoDTO.class
            ).getBody();
        } catch (Exception e) {
            logError("actualizarStock", e);
            throw e;
        }
    }
}
package com.gestiontaller.client.api;

import com.gestiontaller.common.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.common.dto.serie.PerfilSerieDTO;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;
import com.gestiontaller.common.dto.serie.SerieBaseDTO;
import com.gestiontaller.common.model.material.TipoMaterial;
import com.gestiontaller.common.model.serie.TipoSerie;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente API para acceder a los servicios de series
 */
public class SerieApiClient extends BaseApiClient {

    public SerieApiClient(String serverUrl) {
        super(serverUrl, "/api/series");
    }

    /**
     * Obtiene todas las series
     */
    public List<SerieBaseDTO> obtenerTodasLasSeries() {
        try {
            logger.debug("Solicitando todas las series");
            ResponseEntity<List<SerieBaseDTO>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieBaseDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerTodasLasSeries", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene series por tipo de material
     */
    public List<SerieBaseDTO> obtenerSeriesPorTipoMaterial(TipoMaterial tipoMaterial) {
        try {
            logger.debug("Solicitando series por tipo de material: {}", tipoMaterial);
            ResponseEntity<List<SerieBaseDTO>> response = restTemplate.exchange(
                    baseUrl + "/material/" + tipoMaterial,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieBaseDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerSeriesPorTipoMaterial", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una serie por su ID
     */
    public SerieBaseDTO obtenerSeriePorId(Long id) {
        try {
            logger.debug("Solicitando serie con ID: {}", id);
            ResponseEntity<SerieBaseDTO> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    SerieBaseDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerSeriePorId", e);
            throw e;
        }
    }

    /**
     * Obtiene una serie por su código
     */
    public SerieBaseDTO obtenerSeriePorCodigo(String codigo) {
        try {
            logger.debug("Solicitando serie con código: {}", codigo);
            ResponseEntity<SerieBaseDTO> response = restTemplate.exchange(
                    baseUrl + "/codigo/" + codigo,
                    HttpMethod.GET,
                    createEntity(),
                    SerieBaseDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerSeriePorCodigo", e);
            throw e;
        }
    }

    /**
     * Guarda una serie (crea o actualiza)
     */
    public SerieBaseDTO guardarSerie(SerieBaseDTO serieDTO) {
        try {
            logger.debug("Guardando serie: {}", serieDTO.getCodigo());

            if (serieDTO.getId() != null) {
                // Actualización
                ResponseEntity<SerieBaseDTO> response = restTemplate.exchange(
                        baseUrl + "/" + serieDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(serieDTO),
                        SerieBaseDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl,
                        createEntity(serieDTO),
                        SerieBaseDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardarSerie", e);
            throw e;
        }
    }

    /**
     * Elimina una serie
     */
    public void eliminarSerie(Long id) {
        try {
            logger.debug("Eliminando serie con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarSerie", e);
            throw e;
        }
    }

    /**
     * Obtiene todas las series de aluminio
     */
    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        try {
            logger.debug("Solicitando todas las series de aluminio");
            ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                    baseUrl + "/aluminio",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerSeriesAluminio", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene series de aluminio por tipo
     */
    public List<SerieAluminioDTO> obtenerSeriesAluminioPorTipo(TipoSerie tipoSerie) {
        try {
            logger.debug("Solicitando series de aluminio por tipo: {}", tipoSerie);
            ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                    baseUrl + "/aluminio/tipo/" + tipoSerie,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerSeriesAluminioPorTipo", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene series de aluminio por nombre
     */
    public List<SerieAluminioDTO> buscarSeriesAluminioPorNombre(String nombre) {
        try {
            logger.debug("Buscando series de aluminio con nombre: {}", nombre);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/aluminio/buscar")
                    .queryParam("nombre", nombre);

            String url = builder.toUriString();

            ResponseEntity<List<SerieAluminioDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<SerieAluminioDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("buscarSeriesAluminioPorNombre", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una serie de aluminio por su ID
     */
    public SerieAluminioDTO obtenerSerieAluminioPorId(Long id) {
        try {
            logger.debug("Solicitando serie de aluminio con ID: {}", id);
            ResponseEntity<SerieAluminioDTO> response = restTemplate.exchange(
                    baseUrl + "/aluminio/" + id,
                    HttpMethod.GET,
                    createEntity(),
                    SerieAluminioDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("obtenerSerieAluminioPorId", e);
            throw e;
        }
    }

    /**
     * Guarda una serie de aluminio (crea o actualiza)
     */
    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        try {
            logger.debug("Guardando serie de aluminio: {}", serieDTO.getCodigo());

            if (serieDTO.getId() != null) {
                // Actualización
                ResponseEntity<SerieAluminioDTO> response = restTemplate.exchange(
                        baseUrl + "/aluminio/" + serieDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(serieDTO),
                        SerieAluminioDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl + "/aluminio",
                        createEntity(serieDTO),
                        SerieAluminioDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardarSerieAluminio", e);
            throw e;
        }
    }

    /**
     * Crea una serie completa con todos sus componentes
     */
    public SerieAluminioDTO crearSerieCompleta(String codigo, String nombre, String descripcion,
                                               String tipoSerie, boolean roturaPuente, boolean permitePersiana) {
        try {
            logger.debug("Creando serie completa: {} - {}", codigo, nombre);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/aluminio/crear-completa")
                    .queryParam("codigo", codigo)
                    .queryParam("nombre", nombre)
                    .queryParam("descripcion", descripcion)
                    .queryParam("tipoSerie", tipoSerie)
                    .queryParam("roturaPuente", roturaPuente)
                    .queryParam("permitePersiana", permitePersiana);

            String url = builder.toUriString();

            return restTemplate.postForObject(
                    url,
                    createEntity(),
                    SerieAluminioDTO.class
            );
        } catch (Exception e) {
            logError("crearSerieCompleta", e);
            throw e;
        }
    }

    /**
     * Obtiene perfiles de una serie
     */
    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        try {
            logger.debug("Solicitando perfiles para serie ID: {}", serieId);
            ResponseEntity<List<PerfilSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/" + serieId + "/perfiles",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<PerfilSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerPerfilesPorSerieId", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un perfil de serie
     */
    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        try {
            logger.debug("Guardando perfil para serie ID: {}", perfilDTO.getSerieId());

            if (perfilDTO.getId() != null) {
                // Actualización
                ResponseEntity<PerfilSerieDTO> response = restTemplate.exchange(
                        baseUrl + "/perfiles/" + perfilDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(perfilDTO),
                        PerfilSerieDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl + "/perfiles",
                        createEntity(perfilDTO),
                        PerfilSerieDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardarPerfilSerie", e);
            throw e;
        }
    }

    /**
     * Elimina un perfil de serie
     */
    public void eliminarPerfilSerie(Long id) {
        try {
            logger.debug("Eliminando perfil con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/perfiles/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarPerfilSerie", e);
            throw e;
        }
    }

    /**
     * Obtiene descuentos de perfiles para una serie
     */
    public List<DescuentoPerfilSerieDTO> obtenerDescuentosPorSerieId(Long serieId) {
        try {
            logger.debug("Solicitando descuentos para serie ID: {}", serieId);
            ResponseEntity<List<DescuentoPerfilSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/" + serieId + "/descuentos",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<DescuentoPerfilSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerDescuentosPorSerieId", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un descuento de perfil
     */
    public DescuentoPerfilSerieDTO guardarDescuentoPerfilSerie(DescuentoPerfilSerieDTO descuentoDTO) {
        try {
            logger.debug("Guardando descuento para serie ID: {}", descuentoDTO.getSerieId());

            if (descuentoDTO.getId() != null) {
                // Actualización
                ResponseEntity<DescuentoPerfilSerieDTO> response = restTemplate.exchange(
                        baseUrl + "/descuentos/" + descuentoDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(descuentoDTO),
                        DescuentoPerfilSerieDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl + "/descuentos",
                        createEntity(descuentoDTO),
                        DescuentoPerfilSerieDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardarDescuentoPerfilSerie", e);
            throw e;
        }
    }

    /**
     * Elimina un descuento de perfil
     */
    public void eliminarDescuentoPerfilSerie(Long id) {
        try {
            logger.debug("Eliminando descuento con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/descuentos/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarDescuentoPerfilSerie", e);
            throw e;
        }
    }

    /**
     * Obtiene materiales base asociados a una serie
     */
    public List<MaterialBaseSerieDTO> obtenerMaterialesPorSerieId(Long serieId) {
        try {
            logger.debug("Solicitando materiales para serie ID: {}", serieId);
            ResponseEntity<List<MaterialBaseSerieDTO>> response = restTemplate.exchange(
                    baseUrl + "/" + serieId + "/materiales",
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<MaterialBaseSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("obtenerMaterialesPorSerieId", e);
            return new ArrayList<>();
        }
    }

    /**
     * Guarda un material base de serie
     */
    public MaterialBaseSerieDTO guardarMaterialBaseSerie(MaterialBaseSerieDTO materialDTO) {
        try {
            logger.debug("Guardando material para serie ID: {}", materialDTO.getSerieId());

            if (materialDTO.getId() != null) {
                // Actualización
                ResponseEntity<MaterialBaseSerieDTO> response = restTemplate.exchange(
                        baseUrl + "/materiales/" + materialDTO.getId(),
                        HttpMethod.PUT,
                        createEntity(materialDTO),
                        MaterialBaseSerieDTO.class
                );

                return response.getBody();
            } else {
                // Creación
                return restTemplate.postForObject(
                        baseUrl + "/materiales",
                        createEntity(materialDTO),
                        MaterialBaseSerieDTO.class
                );
            }
        } catch (Exception e) {
            logError("guardarMaterialBaseSerie", e);
            throw e;
        }
    }

    /**
     * Elimina un material base de serie
     */
    public void eliminarMaterialBaseSerie(Long id) {
        try {
            logger.debug("Eliminando material con ID: {}", id);
            restTemplate.exchange(
                    baseUrl + "/materiales/" + id,
                    HttpMethod.DELETE,
                    createEntity(),
                    Void.class
            );
        } catch (Exception e) {
            logError("eliminarMaterialBaseSerie", e);
            throw e;
        }
    }

    /**
     * Calcula los materiales necesarios para una ventana de la serie
     */
    public List<MaterialBaseSerieDTO> calcularMaterialesVentana(Long serieId, Integer ancho, Integer alto,
                                                                Integer numHojas, Boolean incluyePersiana) {
        try {
            logger.debug("Calculando materiales para ventana de serie ID: {}", serieId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + serieId + "/calcular-materiales")
                    .queryParam("ancho", ancho)
                    .queryParam("alto", alto)
                    .queryParam("numHojas", numHojas)
                    .queryParam("incluyePersiana", incluyePersiana);

            String url = builder.toUriString();

            ResponseEntity<List<MaterialBaseSerieDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    new ParameterizedTypeReference<List<MaterialBaseSerieDTO>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            logError("calcularMaterialesVentana", e);
            return new ArrayList<>();
        }
    }

    /**
     * Verifica disponibilidad de stock para producir ventana de la serie
     */
    public boolean verificarStockVentana(Long serieId, Integer ancho, Integer alto,
                                         Integer numHojas, Boolean incluyePersiana) {
        try {
            logger.debug("Verificando stock para ventana de serie ID: {}", serieId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + serieId + "/verificar-stock")
                    .queryParam("ancho", ancho)
                    .queryParam("alto", alto)
                    .queryParam("numHojas", numHojas)
                    .queryParam("incluyePersiana", incluyePersiana);

            String url = builder.toUriString();

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    Boolean.class
            );

            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            logError("verificarStockVentana", e);
            return false;
        }
    }

    /**
     * Calcula el precio de una ventana de la serie
     */
    public Double calcularPrecioVentana(Long serieId, Integer ancho, Integer alto,
                                        Integer numHojas, Boolean incluyePersiana,
                                        Double descuentoAdicional) {
        try {
            logger.debug("Calculando precio para ventana de serie ID: {}", serieId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + serieId + "/calcular-precio")
                    .queryParam("ancho", ancho)
                    .queryParam("alto", alto)
                    .queryParam("numHojas", numHojas)
                    .queryParam("incluyePersiana", incluyePersiana);

            if (descuentoAdicional != null) {
                builder.queryParam("descuentoAdicional", descuentoAdicional);
            }

            String url = builder.toUriString();

            ResponseEntity<Double> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    Double.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("calcularPrecioVentana", e);
            return null;
        }
    }

    /**
     * Genera un código para una nueva serie
     */
    public String generarCodigoSerie(TipoMaterial tipoMaterial) {
        try {
            logger.debug("Generando código para serie de tipo: {}", tipoMaterial);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/generar-codigo")
                    .queryParam("tipoMaterial", tipoMaterial);

            String url = builder.toUriString();

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            logError("generarCodigoSerie", e);
            throw e;
        }
    }
}
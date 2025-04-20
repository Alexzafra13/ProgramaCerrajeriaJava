package com.gestiontaller.common.dto.serie;

import lombok.Data;

/**
 * DTO para la transferencia de datos de materiales base asociados a una serie
 */
@Data
public class MaterialBaseSerieDTO {
    private Long id;
    private Long serieId;
    private String nombreSerie;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private String descripcion;
    private String tipoMaterial;
    private Boolean esPredeterminado;
    private String notasTecnicas;
    private Double precioUnitario;
    private Integer stockActual;
}
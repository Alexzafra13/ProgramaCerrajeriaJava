package com.gestiontaller.common.dto.trabajo;

import lombok.Data;

@Data
public class MaterialAsignadoDTO {
    private Long id;
    private Long trabajoId;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidadAsignada;
    private Integer cantidadUsada;
    private String observaciones;
}
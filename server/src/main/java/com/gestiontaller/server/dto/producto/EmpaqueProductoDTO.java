package com.gestiontaller.server.dto.producto;

import lombok.Data;

@Data
public class EmpaqueProductoDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String codigo;
    private String descripcion;
    private Integer unidadesPorEmpaque;
    private Integer cantidadEmpaques;
    private Integer unidadesSueltas;
    private Integer stockTotalUnidades;
}
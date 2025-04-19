package com.gestiontaller.common.dto.calculo;

import lombok.Data;

@Data
public class MaterialAdicionalDTO {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String nombreProducto;
    private String descripcion;
    private Integer cantidad;
    private Integer unidadesIndividuales;
    private Integer cajas;
    private Integer unidadesPorCaja;
    private Double precioUnitario;
    private Double precioTotal;
}
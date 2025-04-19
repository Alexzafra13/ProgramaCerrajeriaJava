package com.gestiontaller.client.model.configuracion;

import lombok.Data;

@Data
public class MaterialConfiguracionDTO {
    private Long id;
    private Long configuracionId;
    private Long productoId;
    private String descripcion;
    private Integer cantidadBase;
    private String formulaCantidad;
    private Double precioUnitario;
}
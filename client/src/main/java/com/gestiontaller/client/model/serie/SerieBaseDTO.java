package com.gestiontaller.client.model.serie;

import lombok.Data;

@Data
public class SerieBaseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private TipoMaterial tipoMaterial;
    private double precioMetroBase;
    private double descuentoSerie;
    private boolean activa;
}
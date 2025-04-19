package com.gestiontaller.common.dto.serie;

import com.gestiontaller.common.model.TipoMaterial;
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
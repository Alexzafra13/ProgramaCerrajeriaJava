package com.gestiontaller.common.dto.calculo;

import com.gestiontaller.common.model.serie.TipoPerfil;
import lombok.Data;

@Data
public class CorteDTO {
    private Long id;
    private Long perfilId;
    private String codigoPerfil;
    private String nombrePerfil;
    private TipoPerfil tipoPerfil;
    private Integer longitud;
    private Integer cantidad;
    private Double angulo1 = 90.0;
    private Double angulo2 = 90.0;
    private String descripcion;
    private String ubicacion;
    private Integer descuentoAplicado;
    private String grupoCorte;
}
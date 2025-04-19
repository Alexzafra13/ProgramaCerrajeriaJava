package com.gestiontaller.common.dto.serie;

import com.gestiontaller.common.model.serie.TipoPerfil;
import lombok.Data;

@Data
public class DescuentoPerfilSerieDTO {
    private Long id;
    private Long serieId;
    private TipoPerfil tipoPerfil;
    private Integer descuentoMilimetros;
    private String descripcion;
    private String formulaCalculo;
}
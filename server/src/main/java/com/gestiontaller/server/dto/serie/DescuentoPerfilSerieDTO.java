package com.gestiontaller.server.dto.serie;

import com.gestiontaller.server.model.serie.TipoPerfil;
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
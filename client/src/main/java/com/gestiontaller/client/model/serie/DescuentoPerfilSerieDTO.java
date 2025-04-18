package com.gestiontaller.client.model.serie;

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
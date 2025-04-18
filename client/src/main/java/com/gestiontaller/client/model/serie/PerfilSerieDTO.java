package com.gestiontaller.client.model.serie;

import lombok.Data;

@Data
public class PerfilSerieDTO {
    private Long id;
    private Long serieId;
    private String nombreSerie;
    private String codigo;
    private String nombre;
    private TipoPerfil tipoPerfil;
    private double pesoMetro;
    private double precioMetro;
    private Integer longitudBarra;
}
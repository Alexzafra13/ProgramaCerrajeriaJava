package com.gestiontaller.server.dto.serie;

import com.gestiontaller.server.model.serie.TipoPerfil;
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
package com.gestiontaller.common.dto.configuracion;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PlantillaConfiguracionSerieDTO {
    private Long id;
    private Long serieId;
    private String nombreSerie;
    private String nombre;
    private Integer numHojas;
    private boolean activa;
    private String descripcion;
    private Set<PerfilConfiguracionDTO> perfiles = new HashSet<>();
    private Set<MaterialConfiguracionDTO> materiales = new HashSet<>();
}
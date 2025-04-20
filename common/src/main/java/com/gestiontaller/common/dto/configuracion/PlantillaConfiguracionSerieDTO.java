
package com.gestiontaller.common.dto.configuracion;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlantillaConfiguracionSerieDTO {
    private Long id;
    private Long serieId;
    private String nombreSerie;
    private String nombre;
    private Integer numHojas;
    private boolean activa;
    private String descripcion;
    private List<PerfilConfiguracionDTO> perfiles = new ArrayList<>();
    private List<MaterialConfiguracionDTO> materiales = new ArrayList<>();
}
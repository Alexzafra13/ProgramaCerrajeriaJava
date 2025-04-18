package com.gestiontaller.server.dto.serie;

import com.gestiontaller.server.model.serie.TipoSerie;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SerieAluminioDTO extends SerieBaseDTO {
    private TipoSerie tipoSerie;
    private double espesorMinimo;
    private double espesorMaximo;
    private String color;
    private boolean roturaPuente;
    private boolean permitePersiana;
    private List<PerfilSerieDTO> perfiles;
    private List<DescuentoPerfilSerieDTO> descuentosPerfiles;
}
package com.gestiontaller.server.dto.calculo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CalculoVentanaDTO {
    private Long id;
    private Long lineaPresupuestoId;
    private Long serieId;
    private String nombreSerie;
    private Long plantillaId;
    private String nombrePlantilla;
    private Integer anchura;
    private Integer altura;
    private Integer anchuraTotal;
    private Integer alturaTotal;
    private Boolean tienePersianas;
    private Integer alturaCajonPersiana;
    private LocalDateTime fechaCalculo;
    private Long usuarioId;
    private String nombreUsuario;
    private String resultadoCalculo;
    private String observaciones;
    private TipoPresupuesto tipoPresupuesto;
    private List<CorteDTO> cortes = new ArrayList<>();
    private List<MaterialAdicionalDTO> materialesAdicionales = new ArrayList<>();
}
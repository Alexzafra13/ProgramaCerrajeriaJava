package com.gestiontaller.common.dto.presupuesto;

import com.gestiontaller.common.model.presupuesto.TipoPresupuesto;
import lombok.Data;

@Data
public class LineaPresupuestoDTO {
    private Long id;
    private Long presupuestoId;
    private Integer orden;
    private TipoPresupuesto tipoPresupuesto;
    private String descripcion;
    private Integer cantidad;
    private Integer ancho;
    private Integer alto;
    private String medidas;
    private Long serieId;
    private String nombreSerie;
    private double precioUnitario;
    private double descuento;
    private double importe;
    private String detallesTecnicos;
    private String tipoVidrio;
    private String colorPerfil;
    private String acabadoEspecial;
    private Boolean incluyePersiana;
    private Long calculoVentanaId;
}
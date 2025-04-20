package com.gestiontaller.common.dto.presupuesto;

import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PresupuestoDTO {
    private Long id;
    private String numero;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaValidez;
    private Long clienteId;
    private String nombreCliente;
    private Long usuarioId;
    private String nombreUsuario;
    private EstadoPresupuesto estado;
    private String observaciones;
    private double baseImponible;
    private double importeIva;
    private double totalPresupuesto;
    private double descuento;
    private Integer tiempoEstimado;
    private String motivoRechazo;
    private String direccionObra;
    private String referencia;
    private List<LineaPresupuestoDTO> lineas = new ArrayList<>();
}
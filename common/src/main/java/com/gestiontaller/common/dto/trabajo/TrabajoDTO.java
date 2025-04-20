package com.gestiontaller.common.dto.trabajo;

import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TrabajoDTO {
    private Long id;
    private String codigo;
    private Long presupuestoId;
    private String numeroPresupuesto;
    private Long clienteId;
    private String nombreCliente;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaProgramada;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String direccionInstalacion;
    private Long estadoId;
    private String nombreEstado;
    private String colorEstado;
    private PrioridadTrabajo prioridad;
    private Long usuarioAsignadoId;
    private String nombreUsuarioAsignado;
    private String observaciones;
    private Integer horasReales;
    private String fotosTrabajoTerminado;
    private String firmaCliente;
    private List<MaterialAsignadoDTO> materialesAsignados = new ArrayList<>();
    private List<CambioEstadoDTO> cambiosEstado = new ArrayList<>();
}
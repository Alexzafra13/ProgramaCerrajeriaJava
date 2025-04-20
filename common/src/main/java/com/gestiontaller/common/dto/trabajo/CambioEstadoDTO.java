package com.gestiontaller.common.dto.trabajo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CambioEstadoDTO {
    private Long id;
    private Long trabajoId;
    private Long estadoAnteriorId;
    private String nombreEstadoAnterior;
    private Long estadoNuevoId;
    private String nombreEstadoNuevo;
    private LocalDateTime fechaCambio;
    private Long usuarioId;
    private String nombreUsuario;
    private String observaciones;
    private String motivoCambio;
    private Boolean automatico;
    private String datosAsociados;
}
package com.gestiontaller.common.dto.inventario;

import com.gestiontaller.common.model.inventario.EstadoRetal;
import com.gestiontaller.common.model.inventario.MotivoDescarte;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RetalDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String tipoProducto;
    private Integer longitud;
    private LocalDateTime fechaCreacion;
    private Long trabajoOrigenId;
    private String codigoTrabajoOrigen;
    private String ubicacion;
    private EstadoRetal estado;
    private MotivoDescarte motivoDescarte;
    private Long usuarioRegistroId;
    private String nombreUsuarioRegistro;
    private String observaciones;
}
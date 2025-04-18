package com.gestiontaller.server.dto.inventario;

import com.gestiontaller.server.model.inventario.TipoMovimiento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoStockDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private double precioUnitario;
    private String documentoOrigen;
    private Long documentoId;
    private Long usuarioId;
    private String nombreUsuario;
    private String observaciones;

}
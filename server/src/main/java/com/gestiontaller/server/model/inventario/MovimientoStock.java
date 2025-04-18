package com.gestiontaller.server.model.inventario;

import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_stock")
@Data
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column
    private double precioUnitario;

    private String documentoOrigen; // Ej: "Trabajo #123", "Presupuesto #456"

    private Long documentoId; // ID del documento origen

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String observaciones;
}
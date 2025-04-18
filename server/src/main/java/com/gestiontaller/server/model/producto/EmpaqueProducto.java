package com.gestiontaller.server.model.producto;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "empaque_producto")
@Data
public class EmpaqueProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    // Número de unidades por empaque (caja/bolsa)
    @Column(nullable = false)
    private Integer unidadesPorEmpaque;

    // Número de empaques en stock
    @Column(nullable = false)
    private Integer cantidadEmpaques;

    // Unidades sueltas (fuera de empaques completos)
    private Integer unidadesSueltas;

    @Column(nullable = false)
    private Integer stockTotalUnidades;

    // Método para actualizar stock total
    @PrePersist
    @PreUpdate
    public void actualizarStockTotal() {
        this.stockTotalUnidades = (cantidadEmpaques * unidadesPorEmpaque) + (unidadesSueltas != null ? unidadesSueltas : 0);
    }
}

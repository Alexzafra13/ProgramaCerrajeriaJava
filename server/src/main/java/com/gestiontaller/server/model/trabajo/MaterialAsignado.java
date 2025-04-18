package com.gestiontaller.server.model.trabajo;

import com.gestiontaller.server.model.producto.Producto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "material_asignado")
@Data
public class MaterialAsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trabajo_id")
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidadAsignada;

    private Integer cantidadUsada;

    private String observaciones;
}
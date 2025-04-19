package com.gestiontaller.server.model.configuracion;

import com.gestiontaller.server.model.producto.Producto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "material_configuracion")
@Data
public class MaterialConfiguracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_id")
    private PlantillaConfiguracionSerie configuracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer cantidadBase;

    private String formulaCantidad;
}
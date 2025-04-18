// src/main/java/com/gestiontaller/model/producto/Producto.java
package com.gestiontaller.server.model.producto;

import com.gestiontaller.server.model.serie.SerieBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "producto")
@Getter @Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String codigoProveedor;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaProducto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private double precioCompra;

    @Column
    private double margenBeneficio;

    @Column
    private double precioVenta;

    private boolean aplicarIva = true;

    private Integer stockMinimo = 0;

    private Integer stockActual = 0;

    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private SerieBase serie; // Para perfiles específicos

    private boolean activo = true;

    private String imagen;

    // Para tornillos y productos en cajas
    private Integer unidadesPorCaja;

    // Para identificación rápida
    private String medida; // ej: "4x30", "M6", etc.

    @Column(columnDefinition = "JSON")
    private String propiedades; // Propiedades adicionales JSON
}
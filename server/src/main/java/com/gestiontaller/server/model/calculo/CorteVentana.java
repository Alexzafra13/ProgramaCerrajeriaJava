// src/main/java/com/gestiontaller/model/calculo/CorteVentana.java
package com.gestiontaller.server.model.calculo;

import com.gestiontaller.common.model.serie.TipoPerfil;
import com.gestiontaller.server.model.producto.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "corte_ventana")
@Getter @Setter
public class CorteVentana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calculo_ventana_id")
    private CalculoVentana calculoVentana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto perfil;

    @Enumerated(EnumType.STRING)
    private TipoPerfil tipoPerfil;

    @Column(nullable = false)
    private Integer longitud; // en mm

    @Column(nullable = false)
    private Integer cantidad;

    private Double angulo1 = 90.0; // Ángulo de corte en un extremo

    private Double angulo2 = 90.0; // Ángulo de corte en el otro extremo

    private String descripcion;

    // Para identificar a qué parte de la ventana pertenece
    private String ubicacion; // Ej: "Marco superior", "Hoja izquierda"

    // Descuento aplicado al corte
    private Integer descuentoAplicado; // en mm

    // Identificador para agrupar cortes relacionados
    private String grupoCorte;
}
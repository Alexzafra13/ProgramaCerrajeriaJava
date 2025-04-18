// src/main/java/com/gestiontaller/model/presupuesto/LineaPresupuesto.java
package com.gestiontaller.server.model.presupuesto;

import com.gestiontaller.server.model.calculo.CalculoVentana;
import com.gestiontaller.server.model.serie.SerieBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "linea_presupuesto")
@Getter @Setter
public class LineaPresupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;

    @Column(nullable = false)
    private Integer orden;

    @Enumerated(EnumType.STRING)
    private TipoPresupuesto tipoPresupuesto;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer cantidad = 1;

    private Integer ancho; // en mm

    private Integer alto; // en mm

    private String medidas; // Representación textual

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @Column(nullable = false)
    private double precioUnitario = 0.0;

    @Column
    private double descuento;

    @Column(nullable = false)
    private double importe = 0.0;

    // Detalles técnicos en formato JSON
    @Column(columnDefinition = "JSON")
    private String detallesTecnicos;

    // Para clientes: información resumida
    private String tipoVidrio;
    private String colorPerfil;
    private String acabadoEspecial;
    private Boolean incluyePersiana = false;

    // Para fabricación: enlace al cálculo detallado
    @OneToOne(mappedBy = "lineaPresupuesto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CalculoVentana calculoVentana;
}
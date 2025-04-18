// src/main/java/com/gestiontaller/model/calculo/CalculoVentana.java
package com.gestiontaller.server.model.calculo;

import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "calculo_ventana")
@Getter @Setter
public class CalculoVentana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linea_presupuesto_id")
    private LineaPresupuesto lineaPresupuesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id")
    private PlantillaCalculo plantilla;

    @Column(nullable = false)
    private Integer anchura; // en mm

    @Column(nullable = false)
    private Integer altura; // en mm

    private Integer anchuraTotal; // incluye cajón de persiana si aplica

    private Integer alturaTotal; // incluye cajón de persiana si aplica

    private Boolean tienePersianas = false;

    private Integer alturaCajonPersiana;

    @Column(nullable = false)
    private LocalDateTime fechaCalculo = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Resultado del cálculo en formato JSON
    @Column(columnDefinition = "JSON")
    private String resultadoCalculo;

    private String observaciones;

    // Cortes necesarios para este cálculo
    @OneToMany(mappedBy = "calculoVentana", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CorteVentana> cortes = new HashSet<>();

    // Materiales adicionales (herrajes, tornillos, etc.)
    @OneToMany(mappedBy = "calculoVentana", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MaterialAdicional> materialesAdicionales = new HashSet<>();
}
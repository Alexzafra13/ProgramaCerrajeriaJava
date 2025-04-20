package com.gestiontaller.server.model.serie;

import com.gestiontaller.server.model.producto.Producto;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que define los materiales base (herrajes, accesorios, tornillería)
 * que son estándar para una serie específica.
 */
@Entity
@Table(name = "material_base_serie")
@Data
public class MaterialBaseSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private String descripcion;

    /**
     * Tipo de material: HERRAJE_BASICO, TORNILLERIA, ACCESORIO, SELLANTE, etc.
     * Permite categorizar los distintos componentes para mejor organización.
     */
    @Column(name = "tipo_material", nullable = false)
    private String tipoMaterial;

    /**
     * Indica si este material es el predeterminado para este tipo en esta serie.
     * Útil cuando hay múltiples opciones del mismo tipo (ej: diferentes cierres).
     */
    @Column(name = "es_predeterminado")
    private Boolean esPredeterminado = true;

    /**
     * Notas técnicas o de instalación para este material en esta serie específica.
     */
    @Column(name = "notas_tecnicas", columnDefinition = "TEXT")
    private String notasTecnicas;
}
package com.gestiontaller.server.model.serie;

import com.gestiontaller.common.model.serie.TipoPerfil;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "descuento_perfil_serie")
@Data
public class DescuentoPerfilSerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil tipoPerfil;

    /**
     * Descuento en milímetros (almacenado en la BD),
     * pero se interpreta como un valor decimal en centímetros.
     * Por ejemplo, un valor de 41 significa 4.1 cm de descuento.
     */
    @Column(nullable = false)
    private Integer descuentoMilimetros;

    /**
     * Obtiene el descuento en centímetros, como un valor decimal.
     * @return El descuento en centímetros (ej: 4.1 cm para un valor almacenado de 41)
     */
    @Transient
    public Double getDescuentoCentimetros() {
        return descuentoMilimetros / 10.0;
    }

    /**
     * Establece el descuento en centímetros, como un valor decimal.
     * @param descuentoCm El descuento en centímetros (ej: 4.1)
     */
    @Transient
    public void setDescuentoCentimetros(Double descuentoCm) {
        if (descuentoCm != null) {
            this.descuentoMilimetros = (int)Math.round(descuentoCm * 10);
        }
    }

    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String formulaCalculo;
}
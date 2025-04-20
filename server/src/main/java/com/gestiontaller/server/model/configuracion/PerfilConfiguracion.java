package com.gestiontaller.server.model.configuracion;

import com.gestiontaller.server.model.serie.PerfilSerie;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "perfil_configuracion")
@Data
public class PerfilConfiguracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_id")
    private PlantillaConfiguracionSerie configuracion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id")
    private PerfilSerie perfil;

    @Column(nullable = false)
    private String tipoPerfil;

    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Descuento en centímetros con decimales.
     * Este valor se almacena directamente como un Double en la base de datos.
     */
    @Column(name = "descuento_cm")
    private Double descuentoCm;

    /**
     * Fórmula de cálculo en JavaScript que opera en centímetros.
     * Las variables en la fórmula (anchoTotal, altoVentana, etc.) se interpretan como centímetros.
     */
    @Column(name = "formula_calculo")
    private String formulaCalculo;
}
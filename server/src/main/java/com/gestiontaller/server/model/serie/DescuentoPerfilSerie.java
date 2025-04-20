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

    @Column(nullable = false)
    private Integer descuentoMilimetros;

    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String formulaCalculo;
}
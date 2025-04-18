package com.gestiontaller.server.model.serie;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "perfil_serie")
@Data
public class PerfilSerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil tipoPerfil;

    @Column(nullable = false)
    private double pesoMetro;

    @Column(nullable = false)
    private double precioMetro;

    private Integer longitudBarra = 6000; // Longitud estándar en mm

    @Column(columnDefinition = "JSON")
    private String propiedades;
}
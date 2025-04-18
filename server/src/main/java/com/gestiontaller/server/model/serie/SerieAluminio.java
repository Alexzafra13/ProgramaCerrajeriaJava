package com.gestiontaller.server.model.serie;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "serie_aluminio")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
public class SerieAluminio extends SerieBase {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSerie tipoSerie;

    @Column(nullable = false)
    private double espesorMinimo;

    @Column(nullable = false)
    private double espesorMaximo;

    private String color;

    private boolean roturaPuente;

    private boolean permitePersiana;

    @Column(columnDefinition = "JSON")
    private String parametrosTecnicos;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PerfilSerie> perfiles = new HashSet<>();

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DescuentoPerfilSerie> descuentosPerfiles = new HashSet<>();
}
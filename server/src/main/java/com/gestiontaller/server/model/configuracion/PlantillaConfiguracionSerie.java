package com.gestiontaller.server.model.configuracion;

import com.gestiontaller.server.model.serie.SerieBase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plantilla_configuracion_serie")
@Data
public class PlantillaConfiguracionSerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer numHojas;

    private boolean activa = true;

    private String descripcion;

    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerfilConfiguracion> perfiles = new ArrayList<>();

    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialConfiguracion> materiales = new ArrayList<>();
}
package com.gestiontaller.server.model.configuracion;

import com.gestiontaller.server.model.serie.SerieBase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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
    private Set<PerfilConfiguracion> perfiles = new HashSet<>();

    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MaterialConfiguracion> materiales = new HashSet<>();

    // Métodos de utilidad para mantener la relación bidireccional
    public void addPerfil(PerfilConfiguracion perfil) {
        perfiles.add(perfil);
        perfil.setConfiguracion(this);
    }

    public void removePerfil(PerfilConfiguracion perfil) {
        perfiles.remove(perfil);
        perfil.setConfiguracion(null);
    }

    public void addMaterial(MaterialConfiguracion material) {
        materiales.add(material);
        material.setConfiguracion(this);
    }

    public void removeMaterial(MaterialConfiguracion material) {
        materiales.remove(material);
        material.setConfiguracion(null);
    }
}
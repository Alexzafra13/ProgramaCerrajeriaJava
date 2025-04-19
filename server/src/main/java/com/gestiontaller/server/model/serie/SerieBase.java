package com.gestiontaller.server.model.serie;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "serie_base")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class SerieBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaterial tipoMaterial;

    @Column(nullable = false)
    private double precioMetroBase;

    @Column
    private double descuentoSerie;

    private boolean activa = true;
}
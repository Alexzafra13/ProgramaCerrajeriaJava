package com.gestiontaller.server.model.trabajo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estado_trabajo")
@Data
public class EstadoTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private String color = "#808080";

    @Column(nullable = false)
    private Integer orden;

    @Column(columnDefinition = "JSON")
    private String notificaciones;

    @Column(columnDefinition = "JSON")
    private String acciones;

    private Boolean requiereAprobacion = false;

    private Boolean esFinal = false;
}
// src/main/java/com/gestiontaller/model/trabajo/Trabajo.java
package com.gestiontaller.server.model.trabajo;

import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trabajo")
@Getter @Setter
public class Trabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDate fechaProgramada;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    private String direccionInstalacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoTrabajo estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadTrabajo prioridad = PrioridadTrabajo.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_asignado_id")
    private Usuario usuarioAsignado;

    private String observaciones;

    private Integer horasReales;

    private String fotosTrabajoTerminado; // URLs o paths

    private String firmaCliente; // Path o blob

    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MaterialAsignado> materialesAsignados = new HashSet<>();

    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CambioEstado> cambiosEstado = new HashSet<>();
}
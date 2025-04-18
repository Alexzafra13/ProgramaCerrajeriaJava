// src/main/java/com/gestiontaller/model/presupuesto/Presupuesto.java
package com.gestiontaller.server.model.presupuesto;

import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "presupuesto")
@Getter @Setter
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDate fechaValidez;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPresupuesto estado = EstadoPresupuesto.PENDIENTE;

    private String observaciones;

    @Column(nullable = false)
    private double baseImponible = 0.0;

    @Column(nullable = false)
    private double importeIva = 0.0;

    @Column(nullable = false)
    private double totalPresupuesto = 0.0;

    @Column
    private double descuento;

    private Integer tiempoEstimado; // en horas

    private String motivoRechazo;

    private String direccionObra;

    private String referencia; // Identificativo para el cliente

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<LineaPresupuesto> lineas = new HashSet<>();
}
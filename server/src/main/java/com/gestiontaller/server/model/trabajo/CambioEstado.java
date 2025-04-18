package com.gestiontaller.server.model.trabajo;

import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "cambio_estado")
@Data
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trabajo_id")
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_anterior_id")
    private EstadoTrabajo estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_nuevo_id")
    private EstadoTrabajo estadoNuevo;

    @Column(nullable = false)
    private LocalDateTime fechaCambio = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String observaciones;

    private String motivoCambio;

    private Boolean automatico = false;

    @Column(columnDefinition = "JSON")
    private String datosAsociados;
}
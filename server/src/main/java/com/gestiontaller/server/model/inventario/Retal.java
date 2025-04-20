// src/main/java/com/gestiontaller/model/inventario/Retal.java
package com.gestiontaller.server.model.inventario;

import com.gestiontaller.common.model.inventario.EstadoRetal;
import com.gestiontaller.common.model.inventario.MotivoDescarte;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "retal")
@Getter @Setter
public class Retal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer longitud; // en mm

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajo_id")
    private Trabajo trabajoOrigen;

    private String ubicacion; // estantería/contenedor

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRetal estado = EstadoRetal.DISPONIBLE;

    @Enumerated(EnumType.STRING)
    private MotivoDescarte motivoDescarte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioRegistro;

    private String observaciones;
}
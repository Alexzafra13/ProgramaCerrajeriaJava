package com.gestiontaller.server.model.cliente;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipoCliente;

    private String nombre;

    private String apellidos;

    private String razonSocial;

    @Column(unique = true)
    private String nifCif;

    private String direccionFiscal;

    private String direccionEnvio;

    private String codigoPostal;

    private String localidad;

    private String provincia;

    private String pais = "España";

    private String telefono1;

    private String telefono2;

    private String email;

    private String web;

    @Column
    private BigDecimal descuento; // Descuento para este cliente

    @Column(nullable = false)
    private LocalDate fechaAlta = LocalDate.now();

    private boolean activo = true;

    private String observaciones;
}
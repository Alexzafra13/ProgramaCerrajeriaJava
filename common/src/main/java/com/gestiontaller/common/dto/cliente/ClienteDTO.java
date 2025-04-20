package com.gestiontaller.common.dto.cliente;

import com.gestiontaller.common.model.cliente.TipoCliente;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClienteDTO {
    private Long id;
    private String codigo;
    private TipoCliente tipoCliente;
    private String nombre;
    private String apellidos;
    private String razonSocial;
    private String nifCif;
    private String direccionFiscal;
    private String direccionEnvio;
    private String codigoPostal;
    private String localidad;
    private String provincia;
    private String pais;
    private String telefono1;
    private String telefono2;
    private String email;
    private String web;
    private BigDecimal descuento;
    private LocalDate fechaAlta;
    private boolean activo;
    private String observaciones;
}
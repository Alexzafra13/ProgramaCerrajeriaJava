package com.gestiontaller.client.model.configuracion;

import lombok.Data;

@Data
public class PerfilConfiguracionDTO {
    private Long id;
    private Long configuracionId;
    private Long perfilId;
    private String codigoPerfil;
    private String nombrePerfil;
    private String tipoPerfil;
    private Integer cantidad;
    private Double descuentoCm;
    private String formulaCalculo;
}
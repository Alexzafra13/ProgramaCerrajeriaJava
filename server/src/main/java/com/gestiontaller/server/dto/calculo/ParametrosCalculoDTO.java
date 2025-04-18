package com.gestiontaller.server.dto.calculo;

import com.gestiontaller.server.model.TipoCristal;
import com.gestiontaller.server.model.presupuesto.TipoPresupuesto;
import lombok.Data;

/**
 * Clase DTO que encapsula los parámetros necesarios para realizar
 * el cálculo de una ventana.
 */
@Data
public class ParametrosCalculoDTO {
    private Long serieId;
    private Integer ancho;
    private Integer alto;
    private TipoPresupuesto tipoPresupuesto;
    private Integer numeroHojas;
    private Boolean incluyePersiana;
    private Integer alturaCajonPersiana;
    private Boolean altoEsTotal = false; // Indica si la medida "alto" incluye el cajón de persiana
    private Boolean generarAcabados = false; // Cálculo para presupuesto/presentación o fabricación
    private TipoCristal tipoCristal = TipoCristal.SIMPLE; // Por defecto es cristal simple

}
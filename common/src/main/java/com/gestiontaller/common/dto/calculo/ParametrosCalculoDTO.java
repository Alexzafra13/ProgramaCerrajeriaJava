package com.gestiontaller.common.dto.calculo;

import com.gestiontaller.common.model.ventana.TipoCristal;
import com.gestiontaller.common.model.presupuesto.TipoPresupuesto;
import lombok.Data;

/**
 * Clase DTO que encapsula los parámetros necesarios para realizar
 * el cálculo de una ventana.
 *
 * Todos los valores dimensionales se expresan en centímetros en la interfaz de usuario,
 * pero se almacenan en milímetros para cálculos internos.
 */
@Data
public class ParametrosCalculoDTO {
    private Long serieId;

    // Dimensiones en mm (internamente), pero se presentan como cm al usuario
    private Integer ancho;  // Ancho en mm
    private Integer alto;   // Alto en mm
    private Integer alturaCajonPersiana; // Altura del cajón en mm

    // Parámetros del tipo de ventana
    private TipoPresupuesto tipoPresupuesto;
    private Integer numeroHojas;
    private Boolean incluyePersiana;
    private Boolean altoEsTotal = false; // Indica si la medida "alto" incluye el cajón de persiana
    private Boolean generarAcabados = false; // Cálculo para presupuesto/presentación o fabricación
    private TipoCristal tipoCristal = TipoCristal.SIMPLE; // Por defecto es cristal simple

    /**
     * Obtiene el ancho en centímetros para presentación al usuario
     * @return ancho en cm con un decimal
     */
    public double getAnchoCm() {
        return ancho != null ? ancho / 10.0 : 0.0;
    }

    /**
     * Establece el ancho a partir de un valor en centímetros
     * @param anchoCm ancho en centímetros
     */
    public void setAnchoCm(double anchoCm) {
        this.ancho = (int) Math.round(anchoCm * 10); // Convierte cm a mm
    }

    /**
     * Obtiene el alto en centímetros para presentación al usuario
     * @return alto en cm con un decimal
     */
    public double getAltoCm() {
        return alto != null ? alto / 10.0 : 0.0;
    }

    /**
     * Establece el alto a partir de un valor en centímetros
     * @param altoCm alto en centímetros
     */
    public void setAltoCm(double altoCm) {
        this.alto = (int) Math.round(altoCm * 10); // Convierte cm a mm
    }

    /**
     * Obtiene la altura del cajón de persiana en centímetros
     * @return altura del cajón en cm con un decimal
     */
    public double getAlturaCajonPersianaCm() {
        return alturaCajonPersiana != null ? alturaCajonPersiana / 10.0 : 0.0;
    }

    /**
     * Establece la altura del cajón de persiana a partir de un valor en centímetros
     * @param alturaCajonCm altura del cajón en centímetros
     */
    public void setAlturaCajonPersianaCm(double alturaCajonCm) {
        this.alturaCajonPersiana = (int) Math.round(alturaCajonCm * 10); // Convierte cm a mm
    }
}
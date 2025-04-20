package com.gestiontaller.common.dto.calculo;

import com.gestiontaller.common.model.serie.TipoPerfil;
import lombok.Data;

@Data
public class CorteDTO {
    private Long id;
    private Long perfilId;
    private String codigoPerfil;
    private String nombrePerfil;
    private TipoPerfil tipoPerfil;

    // Longitud en milímetros (para cálculos internos)
    private Integer longitud;

    private Integer cantidad;
    private Double angulo1 = 90.0;
    private Double angulo2 = 90.0;
    private String descripcion;
    private String ubicacion;

    // Descuento aplicado en milímetros
    private Integer descuentoAplicado;

    private String grupoCorte;

    /**
     * Obtiene la longitud en centímetros con decimales
     * @return longitud en cm
     */
    public double getLongitudCm() {
        return longitud != null ? longitud / 10.0 : 0.0;
    }

    /**
     * Establece la longitud a partir de un valor en centímetros
     * @param longitudCm Longitud en centímetros
     */
    public void setLongitudCm(double longitudCm) {
        this.longitud = (int) Math.round(longitudCm * 10); // Convierte cm a mm
    }

    /**
     * Obtiene el descuento aplicado en centímetros
     * @return descuento en cm
     */
    public double getDescuentoAplicadoCm() {
        return descuentoAplicado != null ? descuentoAplicado / 10.0 : 0.0;
    }

    /**
     * Establece el descuento aplicado a partir de un valor en centímetros
     * @param descuentoCm Descuento en centímetros
     */
    public void setDescuentoAplicadoCm(double descuentoCm) {
        this.descuentoAplicado = (int) Math.round(descuentoCm * 10); // Convierte cm a mm
    }
}
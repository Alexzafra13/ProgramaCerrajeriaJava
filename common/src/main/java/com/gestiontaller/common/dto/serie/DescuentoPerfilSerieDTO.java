package com.gestiontaller.common.dto.serie;

import com.gestiontaller.common.model.serie.TipoPerfil;
import lombok.Data;

@Data
public class DescuentoPerfilSerieDTO {
    private Long id;
    private Long serieId;
    private TipoPerfil tipoPerfil;

    /**
     * Descuento en milímetros (para compatibilidad con la BD),
     * aunque se presenta como valor en centímetros en la UI.
     */
    private Integer descuentoMilimetros;

    /**
     * Valor de descuento en centímetros con decimales, para mostrar en la UI.
     * Este campo no se persiste directamente, sino que se calcula/convierte desde descuentoMilimetros.
     */
    private Double descuentoCentimetros;

    private String descripcion;
    private String formulaCalculo;

    // Getter que calcula el valor en centímetros a partir de milímetros
    public Double getDescuentoCentimetros() {
        if (descuentoMilimetros == null) {
            return null;
        }
        return descuentoMilimetros / 10.0;
    }

    // Setter que actualiza el valor en milímetros a partir de centímetros
    public void setDescuentoCentimetros(Double descuentoCm) {
        if (descuentoCm != null) {
            this.descuentoMilimetros = (int)Math.round(descuentoCm * 10);
        }
    }
}
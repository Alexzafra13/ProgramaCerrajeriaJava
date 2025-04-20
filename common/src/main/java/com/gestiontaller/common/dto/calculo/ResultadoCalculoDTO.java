package com.gestiontaller.common.dto.calculo;

import com.gestiontaller.common.model.ventana.TipoCristal;
import com.gestiontaller.common.model.presupuesto.TipoPresupuesto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResultadoCalculoDTO {
    private TipoPresupuesto tipoPresupuesto;

    // Dimensiones en milímetros (internas)
    private Integer ancho; // Ancho total (incluye cajón persiana si aplica)
    private Integer alto; // Alto total (incluye cajón persiana si aplica)
    private Integer anchoVentana; // Ancho solo de la ventana
    private Integer altoVentana; // Alto solo de la ventana

    // Variables de cálculo
    private Integer numeroHojas;
    private Boolean incluyePersiana;
    private Integer alturaCajon;
    private TipoCristal tipoCristal; // Tipo de cristal (simple o doble)

    // Valores calculados
    private String resumen;
    private List<CorteDTO> cortes = new ArrayList<>();
    private List<MaterialAdicionalDTO> materialesAdicionales = new ArrayList<>();
    private Double precioTotal;
    private String resultadoJson; // Resultado completo en formato JSON para almacenamiento

    // Getters adicionales para valores en centímetros (para presentación)

    /**
     * Obtiene el ancho total en centímetros
     * @return ancho en cm con un decimal
     */
    public double getAnchoCm() {
        return ancho != null ? ancho / 10.0 : 0.0;
    }

    /**
     * Obtiene el alto total en centímetros
     * @return alto en cm con un decimal
     */
    public double getAltoCm() {
        return alto != null ? alto / 10.0 : 0.0;
    }

    /**
     * Obtiene el ancho de la ventana (sin marco) en centímetros
     * @return ancho de ventana en cm con un decimal
     */
    public double getAnchoVentanaCm() {
        return anchoVentana != null ? anchoVentana / 10.0 : 0.0;
    }

    /**
     * Obtiene el alto de la ventana (sin cajón de persiana) en centímetros
     * @return alto de ventana en cm con un decimal
     */
    public double getAltoVentanaCm() {
        return altoVentana != null ? altoVentana / 10.0 : 0.0;
    }

    /**
     * Obtiene la altura del cajón de persiana en centímetros
     * @return altura del cajón en cm con un decimal
     */
    public double getAlturaCajonCm() {
        return alturaCajon != null ? alturaCajon / 10.0 : 0.0;
    }
}
// client/src/main/java/com/gestiontaller/client/model/calculo/ResultadoCalculoDTO.java

package com.gestiontaller.client.model.calculo;

import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.model.presupuesto.TipoPresupuesto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResultadoCalculoDTO {
    private TipoPresupuesto tipoPresupuesto;
    private Integer ancho; // Ancho total (incluye cajón persiana si aplica)
    private Integer alto; // Alto total (incluye cajón persiana si aplica)
    private Integer anchoVentana; // Ancho solo de la ventana
    private Integer altoVentana; // Alto solo de la ventana
    private Integer numeroHojas;
    private Boolean incluyePersiana;
    private Integer alturaCajon;
    private String resumen;
    private List<CorteDTO> cortes = new ArrayList<>();
    private List<MaterialAdicionalDTO> materialesAdicionales = new ArrayList<>();
    private Double precioTotal;
    private String resultadoJson; // Resultado completo en formato JSON para almacenamiento
    private TipoCristal tipoCristal; // Tipo de cristal (simple o doble)
}
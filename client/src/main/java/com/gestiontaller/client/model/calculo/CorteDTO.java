// client/src/main/java/com/gestiontaller/client/model/calculo/CorteDTO.java

package com.gestiontaller.client.model.calculo;

import com.gestiontaller.client.model.serie.TipoPerfil;
import lombok.Data;

@Data
public class CorteDTO {
    private Long id;
    private Long perfilId;
    private String codigoPerfil;
    private String nombrePerfil;
    private TipoPerfil tipoPerfil;
    private Integer longitud;
    private Integer cantidad;
    private Double angulo1 = 90.0;
    private Double angulo2 = 90.0;
    private String descripcion;
    private String ubicacion;
    private Integer descuentoAplicado;
    private String grupoCorte;
}
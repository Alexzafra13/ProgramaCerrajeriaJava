package com.gestiontaller.server.model.calculo;

import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.serie.TipoSerie;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "plantilla_calculo")
@Data
public class PlantillaCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id")
    private SerieBase serie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSerie tipoModelo;

    private Integer numeroHojas;

    private Boolean tienePersianas = false;

    @Column(nullable = false)
    private Boolean activa = true;

    // Configuración específica de la plantilla (fórmulas, componentes, etc.)
    @Column(columnDefinition = "JSON")
    private String configuracion;

    // Imagen o diagrama de la plantilla
    private String imagen;
}
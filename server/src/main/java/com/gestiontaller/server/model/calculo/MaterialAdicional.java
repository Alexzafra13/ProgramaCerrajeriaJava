package com.gestiontaller.server.model.calculo;

import com.gestiontaller.server.model.producto.Producto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "material_adicional")
@Data
public class MaterialAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calculo_ventana_id")
    private CalculoVentana calculoVentana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    private String descripcion;

    // Para tornillos podemos registrar unidades específicas y cajas
    private Integer unidadesIndividuales;

    private Integer cajas;

    private Integer unidadesPorCaja;
}
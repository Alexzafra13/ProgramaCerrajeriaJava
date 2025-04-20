package com.gestiontaller.common.dto.producto;

import com.gestiontaller.common.model.producto.TipoProducto;
import com.gestiontaller.common.model.producto.UnidadMedida;
import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String codigoProveedor;
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private String categoriaNombre;
    private TipoProducto tipo;
    private UnidadMedida unidadMedida;
    private double precioCompra;
    private double margenBeneficio;
    private double precioVenta;
    private boolean aplicarIva;
    private Integer stockMinimo;
    private Integer stockActual;
    private String ubicacion;
    private Long serieId;
    private String serieNombre;
    private boolean activo;
    private String imagen;
    private Integer unidadesPorCaja;
    private String medida;
}
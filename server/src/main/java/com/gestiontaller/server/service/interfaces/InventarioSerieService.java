package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.serie.SerieBase;

import java.util.List;

/**
 * Servicio para gestionar la relación entre series e inventario
 */
public interface InventarioSerieService {

    /**
     * Obtiene todos los productos asociados a una serie
     */
    List<Producto> obtenerProductosPorSerie(Long serieId);

    /**
     * Verifica si hay suficiente stock para fabricar una ventana
     */
    boolean verificarDisponibilidadParaVentana(ResultadoCalculoDTO resultado);

    /**
     * Reserva materiales para una ventana (sin descontar del stock)
     */
    void reservarMaterialesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId);

    /**
     * Consume materiales para una ventana (descuenta del stock)
     */
    void consumirMaterialesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId);

    /**
     * Genera los productos necesarios para una nueva serie
     */
    List<Producto> generarProductosParaSerie(SerieBase serie);
}
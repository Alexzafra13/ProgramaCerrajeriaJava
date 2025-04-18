package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.inventario.MovimientoStockDTO;
import com.gestiontaller.server.dto.inventario.RetalDTO;
import com.gestiontaller.server.model.inventario.EstadoRetal;
import com.gestiontaller.server.model.inventario.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface InventarioService {

    // Movimientos de stock
    MovimientoStockDTO registrarMovimientoStock(MovimientoStockDTO movimientoDTO);

    MovimientoStockDTO obtenerMovimientoPorId(Long id);

    List<MovimientoStockDTO> obtenerMovimientosPorProducto(Long productoId);

    List<MovimientoStockDTO> obtenerMovimientosPorTipo(TipoMovimiento tipo);

    List<MovimientoStockDTO> obtenerMovimientosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Gestión de retales
    RetalDTO registrarRetal(RetalDTO retalDTO);

    RetalDTO obtenerRetalPorId(Long id);

    List<RetalDTO> obtenerRetalesPorProducto(Long productoId);

    List<RetalDTO> obtenerRetalesPorEstado(EstadoRetal estado);

    List<RetalDTO> buscarRetalesDisponiblesParaCorte(Long productoId, Integer longitudMinima);

    RetalDTO actualizarEstadoRetal(Long retalId, EstadoRetal nuevoEstado);

    RetalDTO descartarRetal(Long retalId, String motivo, String observaciones);

    // Utilidades de inventario
    void actualizarStockProducto(Long productoId, Integer cantidad, TipoMovimiento tipo, String origen);

    List<RetalDTO> obtenerRetalesOptimosParaCorte(Long productoId, Integer longitudRequerida, Integer cantidad);
}
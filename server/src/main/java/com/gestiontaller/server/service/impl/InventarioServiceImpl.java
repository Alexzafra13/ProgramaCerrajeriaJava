// src/main/java/com/gestiontaller/server/service/impl/InventarioServiceImpl.java
package com.gestiontaller.server.service.impl;

import com.gestiontaller.common.dto.inventario.MovimientoStockDTO;
import com.gestiontaller.common.dto.inventario.RetalDTO;
import com.gestiontaller.server.mapper.MovimientoStockMapper;
import com.gestiontaller.server.mapper.RetalMapper;
import com.gestiontaller.common.model.inventario.EstadoRetal;
import com.gestiontaller.common.model.inventario.MotivoDescarte;
import com.gestiontaller.server.model.inventario.MovimientoStock;
import com.gestiontaller.server.model.inventario.Retal;
import com.gestiontaller.common.model.inventario.TipoMovimiento;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.repository.inventario.MovimientoStockRepository;
import com.gestiontaller.server.repository.inventario.RetalRepository;
import com.gestiontaller.server.repository.producto.ProductoRepository;
import com.gestiontaller.server.service.interfaces.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final MovimientoStockRepository movimientoStockRepository;
    private final RetalRepository retalRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockMapper movimientoStockMapper;
    private final RetalMapper retalMapper;

    @Autowired
    public InventarioServiceImpl(
            MovimientoStockRepository movimientoStockRepository,
            RetalRepository retalRepository,
            ProductoRepository productoRepository,
            MovimientoStockMapper movimientoStockMapper,
            RetalMapper retalMapper) {
        this.movimientoStockRepository = movimientoStockRepository;
        this.retalRepository = retalRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockMapper = movimientoStockMapper;
        this.retalMapper = retalMapper;
    }

    @Override
    @Transactional
    public MovimientoStockDTO registrarMovimientoStock(MovimientoStockDTO movimientoDTO) {
        MovimientoStock movimiento = movimientoStockMapper.toEntity(movimientoDTO);

        // Asegurar producto
        Producto producto = productoRepository.findById(movimientoDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        movimiento.setProducto(producto);

        // Actualizar stock del producto
        actualizarStockProducto(
                producto.getId(),
                movimiento.getCantidad(),
                movimiento.getTipo(),
                movimiento.getDocumentoOrigen()
        );

        MovimientoStock saved = movimientoStockRepository.save(movimiento);
        return movimientoStockMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoStockDTO obtenerMovimientoPorId(Long id) {
        MovimientoStock movimiento = movimientoStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento de stock no encontrado"));
        return movimientoStockMapper.toDto(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> obtenerMovimientosPorProducto(Long productoId) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByProductoId(productoId);
        return movimientos.stream()
                .map(movimientoStockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> obtenerMovimientosPorTipo(TipoMovimiento tipo) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByTipo(tipo);
        return movimientos.stream()
                .map(movimientoStockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> obtenerMovimientosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByFechaBetween(fechaInicio, fechaFin);
        return movimientos.stream()
                .map(movimientoStockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarStockProducto(Long productoId, Integer cantidad, TipoMovimiento tipo, String origen) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizar según tipo de movimiento
        switch (tipo) {
            case ENTRADA:
                producto.setStockActual(producto.getStockActual() + cantidad);
                break;
            case SALIDA:
                if (producto.getStockActual() < cantidad) {
                    throw new RuntimeException("Stock insuficiente");
                }
                producto.setStockActual(producto.getStockActual() - cantidad);
                break;
            case AJUSTE:
                producto.setStockActual(cantidad);
                break;
            default:
                // Para reservas y liberaciones no se modifica el stock actual
                break;
        }

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public RetalDTO registrarRetal(RetalDTO retalDTO) {
        Retal retal = retalMapper.toEntity(retalDTO);

        // Asegurar producto
        Producto producto = productoRepository.findById(retalDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        retal.setProducto(producto);

        Retal saved = retalRepository.save(retal);
        return retalMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RetalDTO obtenerRetalPorId(Long id) {
        Retal retal = retalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retal no encontrado"));
        return retalMapper.toDto(retal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetalDTO> obtenerRetalesPorProducto(Long productoId) {
        List<Retal> retales = retalRepository.findByProductoId(productoId);
        return retales.stream()
                .map(retalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetalDTO> obtenerRetalesPorEstado(EstadoRetal estado) {
        List<Retal> retales = retalRepository.findByEstado(estado);
        return retales.stream()
                .map(retalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetalDTO> buscarRetalesDisponiblesParaCorte(Long productoId, Integer longitudMinima) {
        return retalRepository.findByEstadoAndProductoIdAndLongitudMinima(
                        EstadoRetal.DISPONIBLE, productoId, longitudMinima).stream()
                .map(retalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RetalDTO actualizarEstadoRetal(Long retalId, EstadoRetal nuevoEstado) {
        Retal retal = retalRepository.findById(retalId)
                .orElseThrow(() -> new RuntimeException("Retal no encontrado"));

        retal.setEstado(nuevoEstado);
        Retal saved = retalRepository.save(retal);
        return retalMapper.toDto(saved);
    }

    @Override
    @Transactional
    public RetalDTO descartarRetal(Long retalId, String motivo, String observaciones) {
        Retal retal = retalRepository.findById(retalId)
                .orElseThrow(() -> new RuntimeException("Retal no encontrado"));

        // Establecer el motivo de descarte basado en el string recibido
        try {
            MotivoDescarte motivoEnum = MotivoDescarte.valueOf(motivo);
            retal.setMotivoDescarte(motivoEnum);
        } catch (IllegalArgumentException e) {
            // Si el motivo no coincide con ningún valor de la enumeración
            throw new RuntimeException("Motivo de descarte inválido: " + motivo);
        }

        // Actualizar el estado a DESCARTADO
        retal.setEstado(EstadoRetal.DESCARTADO);

        // Establecer las observaciones
        retal.setObservaciones(observaciones);

        // Guardar los cambios
        Retal saved = retalRepository.save(retal);

        return retalMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetalDTO> obtenerRetalesOptimosParaCorte(Long productoId, Integer longitudRequerida, Integer cantidad) {
        // Implementa la lógica para encontrar los retales óptimos
        // Por ejemplo:
        List<Retal> retalesOptimos = retalRepository.findByEstadoAndProductoIdAndLongitudMinima(
                EstadoRetal.DISPONIBLE, productoId, longitudRequerida);

        // Limitar por cantidad si es necesario
        if (cantidad != null && cantidad > 0 && retalesOptimos.size() > cantidad) {
            retalesOptimos = retalesOptimos.subList(0, cantidad);
        }

        return retalesOptimos.stream()
                .map(retalMapper::toDto)
                .collect(Collectors.toList());
    }
}
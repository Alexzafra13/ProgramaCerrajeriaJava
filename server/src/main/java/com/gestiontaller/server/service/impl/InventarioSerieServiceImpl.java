package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.inventario.MovimientoStockDTO;
import com.gestiontaller.server.model.inventario.TipoMovimiento;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.repository.producto.ProductoRepository;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;
import com.gestiontaller.server.service.interfaces.InventarioSerieService;
import com.gestiontaller.server.service.interfaces.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que maneja la integración entre series y el inventario
 */
@Service
public class InventarioSerieServiceImpl implements InventarioSerieService {

    private final ProductoRepository productoRepository;
    private final SerieBaseRepository serieRepository;
    private final InventarioService inventarioService;

    @Autowired
    public InventarioSerieServiceImpl(ProductoRepository productoRepository,
                                      SerieBaseRepository serieRepository,
                                      InventarioService inventarioService) {
        this.productoRepository = productoRepository;
        this.serieRepository = serieRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorSerie(Long serieId) {
        SerieBase serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        return productoRepository.findBySerieAndTipo(serie, null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidadParaVentana(ResultadoCalculoDTO resultado) {
        Map<Long, Integer> necesidades = calcularNecesidadesInventario(resultado);

        for (Map.Entry<Long, Integer> necesidad : necesidades.entrySet()) {
            Producto producto = productoRepository.findById(necesidad.getKey())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStockActual() < necesidad.getValue()) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public void reservarMaterialesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId) {
        Map<Long, Integer> necesidades = calcularNecesidadesInventario(resultado);

        for (Map.Entry<Long, Integer> necesidad : necesidades.entrySet()) {
            MovimientoStockDTO movimiento = new MovimientoStockDTO();
            movimiento.setProductoId(necesidad.getKey());
            movimiento.setTipo(TipoMovimiento.RESERVA);
            movimiento.setCantidad(necesidad.getValue());
            movimiento.setDocumentoOrigen("Trabajo");
            movimiento.setDocumentoId(trabajoId);

            inventarioService.registrarMovimientoStock(movimiento);
        }
    }

    @Override
    @Transactional
    public void consumirMaterialesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId) {
        Map<Long, Integer> necesidades = calcularNecesidadesInventario(resultado);

        for (Map.Entry<Long, Integer> necesidad : necesidades.entrySet()) {
            MovimientoStockDTO movimiento = new MovimientoStockDTO();
            movimiento.setProductoId(necesidad.getKey());
            movimiento.setTipo(TipoMovimiento.SALIDA);
            movimiento.setCantidad(necesidad.getValue());
            movimiento.setDocumentoOrigen("Trabajo");
            movimiento.setDocumentoId(trabajoId);

            inventarioService.registrarMovimientoStock(movimiento);
        }
    }

    @Override
    @Transactional
    public List<Producto> generarProductosParaSerie(SerieBase serie) {
        // Esta implementación se completará cuando se defina la estructura exacta
        // de productos por tipo de serie
        return null;
    }

    /**
     * Calcula las necesidades de inventario para una ventana específica
     */
    private Map<Long, Integer> calcularNecesidadesInventario(ResultadoCalculoDTO resultado) {
        Map<Long, Integer> necesidades = new HashMap<>();

        // Agregar perfiles según los cortes
        if (resultado.getCortes() != null) {
            resultado.getCortes().forEach(corte -> {
                Long productoId = corte.getPerfilId();
                Integer cantidad = (int) Math.ceil(corte.getLongitud() / 1000.0 * corte.getCantidad());

                necesidades.merge(productoId, cantidad, Integer::sum);
            });
        }

        // Agregar materiales adicionales
        if (resultado.getMaterialesAdicionales() != null) {
            resultado.getMaterialesAdicionales().forEach(material -> {
                if (material.getProductoId() != null) {
                    necesidades.merge(material.getProductoId(), material.getCantidad(), Integer::sum);
                }
            });
        }

        return necesidades;
    }
}
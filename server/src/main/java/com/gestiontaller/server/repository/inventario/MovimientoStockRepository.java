package com.gestiontaller.server.repository.inventario;

import com.gestiontaller.server.model.inventario.MovimientoStock;
import com.gestiontaller.common.model.inventario.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findByProductoId(Long productoId);

    List<MovimientoStock> findByTipo(TipoMovimiento tipo);

    List<MovimientoStock> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<MovimientoStock> findByProductoIdAndFechaBetween(Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
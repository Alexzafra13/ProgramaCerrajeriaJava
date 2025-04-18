package com.gestiontaller.server.repository.presupuesto;

import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.presupuesto.TipoPresupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaPresupuestoRepository extends JpaRepository<LineaPresupuesto, Long> {

    List<LineaPresupuesto> findByPresupuestoId(Long presupuestoId);

    List<LineaPresupuesto> findBySerieId(Long serieId);

    List<LineaPresupuesto> findByTipoPresupuesto(TipoPresupuesto tipoPresupuesto);

    @Query("SELECT l FROM LineaPresupuesto l WHERE l.presupuesto.id = :presupuestoId ORDER BY l.orden ASC")
    List<LineaPresupuesto> findByPresupuestoIdOrderByOrden(Long presupuestoId);

    @Query("SELECT l FROM LineaPresupuesto l JOIN l.presupuesto p WHERE p.id IN (SELECT t.presupuesto.id FROM Trabajo t WHERE t.id = :trabajoId)")
    List<LineaPresupuesto> findByTrabajoId(Long trabajoId);
}
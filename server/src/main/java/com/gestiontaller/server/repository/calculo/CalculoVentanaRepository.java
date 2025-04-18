package com.gestiontaller.server.repository.calculo;

import com.gestiontaller.server.model.calculo.CalculoVentana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculoVentanaRepository extends JpaRepository<CalculoVentana, Long> {

    List<CalculoVentana> findByLineaPresupuestoId(Long lineaPresupuestoId);

    @Query("SELECT c FROM CalculoVentana c JOIN c.lineaPresupuesto l JOIN l.presupuesto p WHERE p.id = :presupuestoId")
    List<CalculoVentana> findByPresupuestoId(Long presupuestoId);

    // La consulta original asume que Presupuesto tiene una propiedad 'trabajos' que no existe
    // Cambiamos a una consulta que obtenga los cálculos relacionados con un trabajo a través de otra vía
    @Query("SELECT c FROM CalculoVentana c WHERE c.lineaPresupuesto.presupuesto.id IN " +
            "(SELECT t.presupuesto.id FROM Trabajo t WHERE t.id = :trabajoId)")
    List<CalculoVentana> findByTrabajoId(Long trabajoId);

    List<CalculoVentana> findBySerieId(Long serieId);
}
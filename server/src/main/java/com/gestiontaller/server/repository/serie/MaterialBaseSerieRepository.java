package com.gestiontaller.server.repository.serie;

import com.gestiontaller.server.model.serie.MaterialBaseSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialBaseSerieRepository extends JpaRepository<MaterialBaseSerie, Long> {

    /**
     * Busca todos los materiales base asociados a una serie específica
     */
    List<MaterialBaseSerie> findBySerieId(Long serieId);

    /**
     * Busca materiales base de un tipo específico para una serie
     */
    List<MaterialBaseSerie> findBySerieIdAndTipoMaterial(Long serieId, String tipoMaterial);

    /**
     * Busca los materiales base predeterminados para una serie
     */
    List<MaterialBaseSerie> findBySerieIdAndEsPredeterminadoTrue(Long serieId);

    /**
     * Busca un material específico por producto y serie
     */
    MaterialBaseSerie findBySerieIdAndProductoId(Long serieId, Long productoId);

    /**
     * Obtiene una lista de productos recomendados para todas las series activas
     */
    @Query("SELECT m FROM MaterialBaseSerie m JOIN m.serie s WHERE s.activa = true AND m.esPredeterminado = true")
    List<MaterialBaseSerie> findAllPredeterminadosForActiveSeries();
}
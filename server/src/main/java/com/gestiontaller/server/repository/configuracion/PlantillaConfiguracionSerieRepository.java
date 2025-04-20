package com.gestiontaller.server.repository.configuracion;

import com.gestiontaller.server.model.configuracion.PlantillaConfiguracionSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaConfiguracionSerieRepository extends JpaRepository<PlantillaConfiguracionSerie, Long> {
    List<PlantillaConfiguracionSerie> findBySerieIdAndActivaTrue(Long serieId);

    Optional<PlantillaConfiguracionSerie> findBySerieIdAndNumHojas(Long serieId, Integer numHojas);

    // Método para cargar solo la entidad básica
    @Query("SELECT p FROM PlantillaConfiguracionSerie p WHERE p.id = :id")
    Optional<PlantillaConfiguracionSerie> findByIdBasic(Long id);

    // Método para cargar con perfiles
    @Query("SELECT p FROM PlantillaConfiguracionSerie p LEFT JOIN FETCH p.perfiles WHERE p.id = :id")
    Optional<PlantillaConfiguracionSerie> findByIdWithPerfiles(Long id);

    // Método para cargar con materiales
    @Query("SELECT p FROM PlantillaConfiguracionSerie p LEFT JOIN FETCH p.materiales WHERE p.id = :id")
    Optional<PlantillaConfiguracionSerie> findByIdWithMateriales(Long id);
}
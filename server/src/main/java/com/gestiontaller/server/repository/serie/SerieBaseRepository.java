package com.gestiontaller.server.repository.serie;

import com.gestiontaller.server.model.serie.SerieBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieBaseRepository extends JpaRepository<SerieBase, Long> {
    Optional<SerieBase> findByCodigo(String codigo);

    List<SerieBase> findByTipoMaterial(TipoMaterial tipoMaterial);

    List<SerieBase> findByActivaTrue();

    List<SerieBase> findByTipoMaterialAndActivaTrue(TipoMaterial tipoMaterial);
}
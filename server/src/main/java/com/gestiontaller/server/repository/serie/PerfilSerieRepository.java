package com.gestiontaller.server.repository.serie;

import com.gestiontaller.server.model.serie.PerfilSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestiontaller.common.model.serie.TipoPerfil;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilSerieRepository extends JpaRepository<PerfilSerie, Long> {
    List<PerfilSerie> findBySerieId(Long serieId);

    List<PerfilSerie> findBySerieIdAndTipoPerfil(Long serieId, TipoPerfil tipoPerfil);

    Optional<PerfilSerie> findByCodigo(String codigo);
}
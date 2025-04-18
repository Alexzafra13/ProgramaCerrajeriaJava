package com.gestiontaller.server.repository.serie;

import com.gestiontaller.server.model.serie.DescuentoPerfilSerie;
import com.gestiontaller.server.model.serie.TipoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentoPerfilSerieRepository extends JpaRepository<DescuentoPerfilSerie, Long> {
    List<DescuentoPerfilSerie> findBySerieId(Long serieId);

    Optional<DescuentoPerfilSerie> findBySerieIdAndTipoPerfil(Long serieId, TipoPerfil tipoPerfil);
}
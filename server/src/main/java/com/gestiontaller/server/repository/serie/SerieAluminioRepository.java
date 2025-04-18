package com.gestiontaller.server.repository.serie;

import com.gestiontaller.server.model.serie.SerieAluminio;
import com.gestiontaller.server.model.serie.TipoSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieAluminioRepository extends JpaRepository<SerieAluminio, Long> {
    List<SerieAluminio> findByActivaTrue();

    List<SerieAluminio> findByTipoSerieAndActivaTrue(TipoSerie tipoSerie);

    List<SerieAluminio> findByPermitePersianaTrue();

    List<SerieAluminio> findByRoturaPuenteTrue();
}
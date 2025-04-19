package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.server.dto.serie.PerfilSerieDTO;
import com.gestiontaller.server.dto.serie.SerieAluminioDTO;
import com.gestiontaller.server.dto.serie.SerieBaseDTO;
import com.gestiontaller.server.model.TipoMaterial;
import com.gestiontaller.server.model.serie.TipoSerie;

import java.util.List;

public interface SerieService {
    List<SerieBaseDTO> obtenerTodasLasSeries();

    List<SerieBaseDTO> obtenerSeriesPorTipoMaterial(TipoMaterial tipoMaterial);

    SerieBaseDTO obtenerSeriePorId(Long id);

    SerieBaseDTO guardarSerie(SerieBaseDTO serieDTO);

    void eliminarSerie(Long id);

    List<SerieAluminioDTO> obtenerSeriesAluminio();

    List<SerieAluminioDTO> obtenerSeriesAluminioPorTipo(TipoSerie tipoSerie);

    SerieAluminioDTO obtenerSerieAluminioPorId(Long id);

    SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO);

    List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId);

    PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO);

    void eliminarPerfilSerie(Long id);

    List<DescuentoPerfilSerieDTO> obtenerDescuentosPorSerieId(Long serieId);

    DescuentoPerfilSerieDTO guardarDescuentoPerfilSerie(DescuentoPerfilSerieDTO descuentoDTO);
    /**
     * Crea una serie completa de aluminio con todos sus componentes y productos
     */
    SerieAluminioDTO crearSerieCompleta(String codigo, String nombre, String descripcion,
                                        TipoSerie tipoSerie, boolean roturaPuente, boolean permitePersiana);

    void eliminarDescuentoPerfilSerie(Long id);
}
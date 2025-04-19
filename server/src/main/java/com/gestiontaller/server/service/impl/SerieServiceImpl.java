package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.server.dto.serie.PerfilSerieDTO;
import com.gestiontaller.server.dto.serie.SerieAluminioDTO;
import com.gestiontaller.server.dto.serie.SerieBaseDTO;
import com.gestiontaller.server.mapper.DescuentoPerfilSerieMapper;
import com.gestiontaller.server.mapper.PerfilSerieMapper;
import com.gestiontaller.server.mapper.SerieAluminioMapper;
import com.gestiontaller.server.mapper.SerieBaseMapper;
import com.gestiontaller.server.model.TipoMaterial;
import com.gestiontaller.server.model.serie.DescuentoPerfilSerie;
import com.gestiontaller.server.model.serie.PerfilSerie;
import com.gestiontaller.server.model.serie.SerieAluminio;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.serie.TipoSerie;
import com.gestiontaller.server.repository.serie.DescuentoPerfilSerieRepository;
import com.gestiontaller.server.repository.serie.PerfilSerieRepository;
import com.gestiontaller.server.repository.serie.SerieAluminioRepository;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;
import com.gestiontaller.server.service.interfaces.InventarioSerieService;
import com.gestiontaller.server.service.interfaces.SerieService;
import com.gestiontaller.server.util.generador.GeneradorSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieServiceImpl implements SerieService {

    private final SerieBaseRepository serieBaseRepository;
    private final SerieAluminioRepository serieAluminioRepository;
    private final PerfilSerieRepository perfilSerieRepository;
    private final DescuentoPerfilSerieRepository descuentoPerfilSerieRepository;
    private final SerieBaseMapper serieBaseMapper;
    private final SerieAluminioMapper serieAluminioMapper;
    private final PerfilSerieMapper perfilSerieMapper;
    private final DescuentoPerfilSerieMapper descuentoPerfilSerieMapper;

    @Autowired
    private GeneradorSeries generadorSeries;

    @Autowired
    private InventarioSerieService inventarioSerieService;

    @Autowired
    public SerieServiceImpl(
            SerieBaseRepository serieBaseRepository,
            SerieAluminioRepository serieAluminioRepository,
            PerfilSerieRepository perfilSerieRepository,
            DescuentoPerfilSerieRepository descuentoPerfilSerieRepository,
            SerieBaseMapper serieBaseMapper,
            SerieAluminioMapper serieAluminioMapper,
            PerfilSerieMapper perfilSerieMapper,
            DescuentoPerfilSerieMapper descuentoPerfilSerieMapper) {
        this.serieBaseRepository = serieBaseRepository;
        this.serieAluminioRepository = serieAluminioRepository;
        this.perfilSerieRepository = perfilSerieRepository;
        this.descuentoPerfilSerieRepository = descuentoPerfilSerieRepository;
        this.serieBaseMapper = serieBaseMapper;
        this.serieAluminioMapper = serieAluminioMapper;
        this.perfilSerieMapper = perfilSerieMapper;
        this.descuentoPerfilSerieMapper = descuentoPerfilSerieMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieBaseDTO> obtenerTodasLasSeries() {
        return serieBaseRepository.findAll().stream()
                .map(serieBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieBaseDTO> obtenerSeriesPorTipoMaterial(TipoMaterial tipoMaterial) {
        return serieBaseRepository.findByTipoMaterial(tipoMaterial).stream()
                .map(serieBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SerieBaseDTO obtenerSeriePorId(Long id) {
        return serieBaseRepository.findById(id)
                .map(serieBaseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public SerieBaseDTO guardarSerie(SerieBaseDTO serieDTO) {
        SerieBase serie = serieDTO.getId() != null
                ? serieBaseRepository.findById(serieDTO.getId())
                .orElse(serieBaseMapper.toEntity(serieDTO))
                : serieBaseMapper.toEntity(serieDTO);

        serieBaseMapper.updateEntityFromDto(serieDTO, serie);
        SerieBase savedSerie = serieBaseRepository.save(serie);
        return serieBaseMapper.toDto(savedSerie);
    }

    @Override
    @Transactional
    public void eliminarSerie(Long id) {
        serieBaseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        List<SerieAluminio> series = serieAluminioRepository.findAll();
        return series.stream()
                .map(serie -> {
                    SerieAluminioDTO dto = serieAluminioMapper.toDto(serie);
                    dto.setPerfiles(obtenerPerfilesPorSerieId(serie.getId()));
                    dto.setDescuentosPerfiles(obtenerDescuentosPorSerieId(serie.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieAluminioDTO> obtenerSeriesAluminioPorTipo(TipoSerie tipoSerie) {
        List<SerieAluminio> series = serieAluminioRepository.findByTipoSerieAndActivaTrue(tipoSerie);
        return series.stream()
                .map(serie -> {
                    SerieAluminioDTO dto = serieAluminioMapper.toDto(serie);
                    dto.setPerfiles(obtenerPerfilesPorSerieId(serie.getId()));
                    dto.setDescuentosPerfiles(obtenerDescuentosPorSerieId(serie.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SerieAluminioDTO obtenerSerieAluminioPorId(Long id) {
        SerieAluminio serie = serieAluminioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie de aluminio no encontrada con ID: " + id));
        SerieAluminioDTO dto = serieAluminioMapper.toDto(serie);
        dto.setPerfiles(obtenerPerfilesPorSerieId(id));
        dto.setDescuentosPerfiles(obtenerDescuentosPorSerieId(id));
        return dto;
    }

    @Override
    @Transactional
    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        SerieAluminio serie;

        if (serieDTO.getId() != null) {
            serie = serieAluminioRepository.findById(serieDTO.getId())
                    .orElse(serieAluminioMapper.toEntity(serieDTO));
            serieAluminioMapper.updateEntityFromDto(serieDTO, serie);
        } else {
            serie = serieAluminioMapper.toEntity(serieDTO);
        }

        SerieAluminio savedSerie = serieAluminioRepository.save(serie);
        SerieAluminioDTO savedDto = serieAluminioMapper.toDto(savedSerie);

        // Guardar perfiles si vienen incluidos
        if (serieDTO.getPerfiles() != null) {
            serieDTO.getPerfiles().forEach(perfilDTO -> {
                perfilDTO.setSerieId(savedSerie.getId());
                guardarPerfilSerie(perfilDTO);
            });
        }

        // Guardar descuentos si vienen incluidos
        if (serieDTO.getDescuentosPerfiles() != null) {
            serieDTO.getDescuentosPerfiles().forEach(descuentoDTO -> {
                descuentoDTO.setSerieId(savedSerie.getId());
                guardarDescuentoPerfilSerie(descuentoDTO);
            });
        }

        // Obtener los perfiles y descuentos actualizados
        savedDto.setPerfiles(obtenerPerfilesPorSerieId(savedSerie.getId()));
        savedDto.setDescuentosPerfiles(obtenerDescuentosPorSerieId(savedSerie.getId()));

        return savedDto;
    }

    @Override
    @Transactional
    public SerieAluminioDTO crearSerieCompleta(String codigo, String nombre, String descripcion,
                                               TipoSerie tipoSerie, boolean roturaPuente, boolean permitePersiana) {
        // Verificar que no exista ya una serie con ese código
        if (serieBaseRepository.findByCodigo(codigo).isPresent()) {
            throw new RuntimeException("Ya existe una serie con el código: " + codigo);
        }

        // Generar la serie completa con todos sus componentes
        SerieAluminioDTO serieDTO = generadorSeries.generarSerieAluminio(
                codigo, nombre, descripcion, tipoSerie, roturaPuente, permitePersiana);

        // Guardar la serie
        SerieAluminioDTO serieSaved = guardarSerieAluminio(serieDTO);

        // Generar productos para esta serie en el inventario
        SerieBase serie = serieBaseRepository.findById(serieSaved.getId())
                .orElseThrow(() -> new RuntimeException("Serie no encontrada tras guardar"));
        inventarioSerieService.generarProductosParaSerie(serie);

        return serieSaved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        return perfilSerieRepository.findBySerieId(serieId).stream()
                .map(perfilSerieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        PerfilSerie perfil;

        if (perfilDTO.getId() != null) {
            perfil = perfilSerieRepository.findById(perfilDTO.getId())
                    .orElse(perfilSerieMapper.toEntity(perfilDTO));
            perfilSerieMapper.updateEntityFromDto(perfilDTO, perfil);
        } else {
            perfil = perfilSerieMapper.toEntity(perfilDTO);
        }

        PerfilSerie savedPerfil = perfilSerieRepository.save(perfil);
        return perfilSerieMapper.toDto(savedPerfil);
    }

    @Override
    @Transactional
    public void eliminarPerfilSerie(Long id) {
        perfilSerieRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DescuentoPerfilSerieDTO> obtenerDescuentosPorSerieId(Long serieId) {
        return descuentoPerfilSerieRepository.findBySerieId(serieId).stream()
                .map(descuentoPerfilSerieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DescuentoPerfilSerieDTO guardarDescuentoPerfilSerie(DescuentoPerfilSerieDTO descuentoDTO) {
        DescuentoPerfilSerie descuento;

        if (descuentoDTO.getId() != null) {
            descuento = descuentoPerfilSerieRepository.findById(descuentoDTO.getId())
                    .orElse(descuentoPerfilSerieMapper.toEntity(descuentoDTO));
            descuentoPerfilSerieMapper.updateEntityFromDto(descuentoDTO, descuento);
        } else {
            descuento = descuentoPerfilSerieMapper.toEntity(descuentoDTO);
        }

        DescuentoPerfilSerie savedDescuento = descuentoPerfilSerieRepository.save(descuento);
        return descuentoPerfilSerieMapper.toDto(savedDescuento);
    }

    @Override
    @Transactional
    public void eliminarDescuentoPerfilSerie(Long id) {
        descuentoPerfilSerieRepository.deleteById(id);
    }
}
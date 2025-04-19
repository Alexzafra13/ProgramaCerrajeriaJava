package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.server.dto.serie.PerfilSerieDTO;
import com.gestiontaller.server.dto.serie.SerieAluminioDTO;
import com.gestiontaller.server.dto.serie.SerieBaseDTO;
import com.gestiontaller.server.exception.DuplicateEntityException;
import com.gestiontaller.server.exception.SerieNotFoundException;
import com.gestiontaller.server.mapper.DescuentoPerfilSerieMapper;
import com.gestiontaller.server.mapper.PerfilSerieMapper;
import com.gestiontaller.server.mapper.SerieAluminioMapper;
import com.gestiontaller.server.mapper.SerieBaseMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieServiceImpl implements SerieService {

    private static final Logger logger = LoggerFactory.getLogger(SerieServiceImpl.class);

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
        logger.debug("Obteniendo todas las series");
        return serieBaseRepository.findAll().stream()
                .map(serieBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieBaseDTO> obtenerSeriesPorTipoMaterial(TipoMaterial tipoMaterial) {
        logger.debug("Obteniendo series por tipo de material: {}", tipoMaterial);
        return serieBaseRepository.findByTipoMaterial(tipoMaterial).stream()
                .map(serieBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SerieBaseDTO obtenerSeriePorId(Long id) {
        logger.debug("Obteniendo serie con ID: {}", id);
        return serieBaseRepository.findById(id)
                .map(serieBaseMapper::toDto)
                .orElseThrow(() -> new SerieNotFoundException(id));
    }

    @Override
    @Transactional
    public SerieBaseDTO guardarSerie(SerieBaseDTO serieDTO) {
        logger.debug("Guardando serie: {}", serieDTO.getCodigo());
        SerieBase serie = serieDTO.getId() != null
                ? serieBaseRepository.findById(serieDTO.getId())
                .orElse(serieBaseMapper.toEntity(serieDTO))
                : serieBaseMapper.toEntity(serieDTO);

        serieBaseMapper.updateEntityFromDto(serieDTO, serie);
        SerieBase savedSerie = serieBaseRepository.save(serie);
        logger.info("Serie guardada con ID: {}", savedSerie.getId());
        return serieBaseMapper.toDto(savedSerie);
    }

    @Override
    @Transactional
    public void eliminarSerie(Long id) {
        logger.debug("Eliminando serie con ID: {}", id);
        if (!serieBaseRepository.existsById(id)) {
            throw new SerieNotFoundException(id);
        }
        serieBaseRepository.deleteById(id);
        logger.info("Serie eliminada con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SerieAluminioDTO> obtenerSeriesAluminio() {
        logger.debug("Obteniendo todas las series de aluminio");
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
        logger.debug("Obteniendo series de aluminio por tipo: {}", tipoSerie);
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
        logger.debug("Obteniendo serie de aluminio con ID: {}", id);
        SerieAluminio serie = serieAluminioRepository.findById(id)
                .orElseThrow(() -> new SerieNotFoundException(id));
        SerieAluminioDTO dto = serieAluminioMapper.toDto(serie);
        dto.setPerfiles(obtenerPerfilesPorSerieId(id));
        dto.setDescuentosPerfiles(obtenerDescuentosPorSerieId(id));
        return dto;
    }

    @Override
    @Transactional
    public SerieAluminioDTO guardarSerieAluminio(SerieAluminioDTO serieDTO) {
        logger.debug("Guardando serie de aluminio: {}", serieDTO.getCodigo());
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

        logger.info("Serie de aluminio guardada con ID: {}", savedSerie.getId());
        return savedDto;
    }

    @Override
    @Transactional
    public SerieAluminioDTO crearSerieCompleta(String codigo, String nombre, String descripcion,
                                               TipoSerie tipoSerie, boolean roturaPuente, boolean permitePersiana) {
        logger.debug("Creando serie completa: {} - {}", codigo, nombre);
        // Verificar que no exista ya una serie con ese código
        if (serieBaseRepository.findByCodigo(codigo).isPresent()) {
            throw new DuplicateEntityException("Serie", "código", codigo);
        }

        // Generar la serie completa con todos sus componentes
        SerieAluminioDTO serieDTO = generadorSeries.generarSerieAluminio(
                codigo, nombre, descripcion, tipoSerie, roturaPuente, permitePersiana);

        // Guardar la serie
        SerieAluminioDTO serieSaved = guardarSerieAluminio(serieDTO);

        // Generar productos para esta serie en el inventario
        SerieBase serie = serieBaseRepository.findById(serieSaved.getId())
                .orElseThrow(() -> new SerieNotFoundException(serieSaved.getId()));
        inventarioSerieService.generarProductosParaSerie(serie);

        logger.info("Serie completa creada con ID: {}", serieSaved.getId());
        return serieSaved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilSerieDTO> obtenerPerfilesPorSerieId(Long serieId) {
        logger.debug("Obteniendo perfiles para serie ID: {}", serieId);
        return perfilSerieRepository.findBySerieId(serieId).stream()
                .map(perfilSerieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PerfilSerieDTO guardarPerfilSerie(PerfilSerieDTO perfilDTO) {
        logger.debug("Guardando perfil: {} para serie ID: {}", perfilDTO.getCodigo(), perfilDTO.getSerieId());
        PerfilSerie perfil;

        if (perfilDTO.getId() != null) {
            perfil = perfilSerieRepository.findById(perfilDTO.getId())
                    .orElse(perfilSerieMapper.toEntity(perfilDTO));
            perfilSerieMapper.updateEntityFromDto(perfilDTO, perfil);
        } else {
            perfil = perfilSerieMapper.toEntity(perfilDTO);
        }

        PerfilSerie savedPerfil = perfilSerieRepository.save(perfil);
        logger.info("Perfil guardado con ID: {}", savedPerfil.getId());
        return perfilSerieMapper.toDto(savedPerfil);
    }

    @Override
    @Transactional
    public void eliminarPerfilSerie(Long id) {
        logger.debug("Eliminando perfil con ID: {}", id);
        perfilSerieRepository.deleteById(id);
        logger.info("Perfil eliminado con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DescuentoPerfilSerieDTO> obtenerDescuentosPorSerieId(Long serieId) {
        logger.debug("Obteniendo descuentos para serie ID: {}", serieId);
        return descuentoPerfilSerieRepository.findBySerieId(serieId).stream()
                .map(descuentoPerfilSerieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DescuentoPerfilSerieDTO guardarDescuentoPerfilSerie(DescuentoPerfilSerieDTO descuentoDTO) {
        logger.debug("Guardando descuento para serie ID: {} y tipo perfil: {}",
                descuentoDTO.getSerieId(), descuentoDTO.getTipoPerfil());
        DescuentoPerfilSerie descuento;

        if (descuentoDTO.getId() != null) {
            descuento = descuentoPerfilSerieRepository.findById(descuentoDTO.getId())
                    .orElse(descuentoPerfilSerieMapper.toEntity(descuentoDTO));
            descuentoPerfilSerieMapper.updateEntityFromDto(descuentoDTO, descuento);
        } else {
            descuento = descuentoPerfilSerieMapper.toEntity(descuentoDTO);
        }

        DescuentoPerfilSerie savedDescuento = descuentoPerfilSerieRepository.save(descuento);
        logger.info("Descuento guardado con ID: {}", savedDescuento.getId());
        return descuentoPerfilSerieMapper.toDto(savedDescuento);
    }

    @Override
    @Transactional
    public void eliminarDescuentoPerfilSerie(Long id) {
        logger.debug("Eliminando descuento con ID: {}", id);
        descuentoPerfilSerieRepository.deleteById(id);
        logger.info("Descuento eliminado con ID: {}", id);
    }
}
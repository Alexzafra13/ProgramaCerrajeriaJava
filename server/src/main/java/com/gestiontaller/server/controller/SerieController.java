package com.gestiontaller.server.controller;

import com.gestiontaller.common.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.common.dto.serie.PerfilSerieDTO;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;
import com.gestiontaller.common.dto.serie.SerieBaseDTO;
import com.gestiontaller.common.model.material.TipoMaterial;
import com.gestiontaller.common.model.serie.TipoSerie;
import com.gestiontaller.server.service.interfaces.SerieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    private static final Logger logger = LoggerFactory.getLogger(SerieController.class);

    private final SerieService serieService;

    @Autowired
    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping
    public ResponseEntity<List<SerieBaseDTO>> obtenerTodasLasSeries() {
        logger.debug("REST - Obteniendo todas las series");
        return ResponseEntity.ok(serieService.obtenerTodasLasSeries());
    }

    @GetMapping("/material/{tipoMaterial}")
    public ResponseEntity<List<SerieBaseDTO>> obtenerSeriesPorTipoMaterial(
            @PathVariable TipoMaterial tipoMaterial) {
        logger.debug("REST - Obteniendo series por tipo material: {}", tipoMaterial);
        return ResponseEntity.ok(serieService.obtenerSeriesPorTipoMaterial(tipoMaterial));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerieBaseDTO> obtenerSeriePorId(@PathVariable Long id) {
        logger.debug("REST - Obteniendo serie con ID: {}", id);
        return ResponseEntity.ok(serieService.obtenerSeriePorId(id));
    }

    @PostMapping
    public ResponseEntity<SerieBaseDTO> guardarSerie(@RequestBody SerieBaseDTO serieDTO) {
        logger.debug("REST - Guardando serie: {}", serieDTO.getCodigo());
        return ResponseEntity.ok(serieService.guardarSerie(serieDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSerie(@PathVariable Long id) {
        logger.debug("REST - Eliminando serie con ID: {}", id);
        serieService.eliminarSerie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/aluminio")
    public ResponseEntity<List<SerieAluminioDTO>> obtenerSeriesAluminio() {
        logger.debug("REST - Obteniendo todas las series de aluminio");
        return ResponseEntity.ok(serieService.obtenerSeriesAluminio());
    }

    @GetMapping("/aluminio/tipo/{tipoSerie}")
    public ResponseEntity<List<SerieAluminioDTO>> obtenerSeriesAluminioPorTipo(
            @PathVariable TipoSerie tipoSerie) {
        logger.debug("REST - Obteniendo series de aluminio por tipo: {}", tipoSerie);
        return ResponseEntity.ok(serieService.obtenerSeriesAluminioPorTipo(tipoSerie));
    }

    @GetMapping("/aluminio/{id}")
    public ResponseEntity<SerieAluminioDTO> obtenerSerieAluminioPorId(@PathVariable Long id) {
        logger.debug("REST - Obteniendo serie de aluminio con ID: {}", id);
        return ResponseEntity.ok(serieService.obtenerSerieAluminioPorId(id));
    }

    @PostMapping("/aluminio")
    public ResponseEntity<SerieAluminioDTO> guardarSerieAluminio(@RequestBody SerieAluminioDTO serieDTO) {
        logger.debug("REST - Guardando serie de aluminio: {}", serieDTO.getCodigo());
        return ResponseEntity.ok(serieService.guardarSerieAluminio(serieDTO));
    }

    @PostMapping("/aluminio/completa")
    public ResponseEntity<SerieAluminioDTO> crearSerieCompleta(@RequestBody Map<String, Object> datos) {
        String codigo = (String) datos.get("codigo");
        String nombre = (String) datos.get("nombre");
        String descripcion = (String) datos.get("descripcion");
        TipoSerie tipoSerie = TipoSerie.valueOf((String) datos.get("tipoSerie"));
        boolean roturaPuente = (boolean) datos.getOrDefault("roturaPuente", false);
        boolean permitePersiana = (boolean) datos.getOrDefault("permitePersiana", false);

        logger.debug("REST - Creando serie completa: {} - {}", codigo, nombre);
        return ResponseEntity.ok(serieService.crearSerieCompleta(
                codigo, nombre, descripcion, tipoSerie, roturaPuente, permitePersiana));
    }

    @GetMapping("/{serieId}/perfiles")
    public ResponseEntity<List<PerfilSerieDTO>> obtenerPerfilesPorSerieId(@PathVariable Long serieId) {
        logger.debug("REST - Obteniendo perfiles para serie ID: {}", serieId);
        return ResponseEntity.ok(serieService.obtenerPerfilesPorSerieId(serieId));
    }

    @PostMapping("/perfiles")
    public ResponseEntity<PerfilSerieDTO> guardarPerfilSerie(@RequestBody PerfilSerieDTO perfilDTO) {
        logger.debug("REST - Guardando perfil: {} para serie ID: {}",
                perfilDTO.getCodigo(), perfilDTO.getSerieId());
        return ResponseEntity.ok(serieService.guardarPerfilSerie(perfilDTO));
    }

    @DeleteMapping("/perfiles/{id}")
    public ResponseEntity<Void> eliminarPerfilSerie(@PathVariable Long id) {
        logger.debug("REST - Eliminando perfil con ID: {}", id);
        serieService.eliminarPerfilSerie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{serieId}/descuentos")
    public ResponseEntity<List<DescuentoPerfilSerieDTO>> obtenerDescuentosPorSerieId(@PathVariable Long serieId) {
        logger.debug("REST - Obteniendo descuentos para serie ID: {}", serieId);
        return ResponseEntity.ok(serieService.obtenerDescuentosPorSerieId(serieId));
    }

    @PostMapping("/descuentos")
    public ResponseEntity<DescuentoPerfilSerieDTO> guardarDescuentoPerfilSerie(
            @RequestBody DescuentoPerfilSerieDTO descuentoDTO) {
        logger.debug("REST - Guardando descuento para serie ID: {} y tipo perfil: {}",
                descuentoDTO.getSerieId(), descuentoDTO.getTipoPerfil());
        return ResponseEntity.ok(serieService.guardarDescuentoPerfilSerie(descuentoDTO));
    }

    @DeleteMapping("/descuentos/{id}")
    public ResponseEntity<Void> eliminarDescuentoPerfilSerie(@PathVariable Long id) {
        logger.debug("REST - Eliminando descuento con ID: {}", id);
        serieService.eliminarDescuentoPerfilSerie(id);
        return ResponseEntity.ok().build();
    }
}
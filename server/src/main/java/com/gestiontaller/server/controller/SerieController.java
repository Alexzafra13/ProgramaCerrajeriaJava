package com.gestiontaller.server.controller;

import com.gestiontaller.server.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.server.dto.serie.PerfilSerieDTO;
import com.gestiontaller.server.dto.serie.SerieAluminioDTO;
import com.gestiontaller.server.dto.serie.SerieBaseDTO;
import com.gestiontaller.server.model.TipoMaterial;
import com.gestiontaller.server.model.serie.TipoSerie;
import com.gestiontaller.server.service.interfaces.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    private final SerieService serieService;

    @Autowired
    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping
    public ResponseEntity<List<SerieBaseDTO>> obtenerTodasLasSeries() {
        return ResponseEntity.ok(serieService.obtenerTodasLasSeries());
    }

    @GetMapping("/material/{tipoMaterial}")
    public ResponseEntity<List<SerieBaseDTO>> obtenerSeriesPorTipoMaterial(
            @PathVariable TipoMaterial tipoMaterial) {
        return ResponseEntity.ok(serieService.obtenerSeriesPorTipoMaterial(tipoMaterial));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerieBaseDTO> obtenerSeriePorId(@PathVariable Long id) {
        return ResponseEntity.ok(serieService.obtenerSeriePorId(id));
    }

    @PostMapping
    public ResponseEntity<SerieBaseDTO> guardarSerie(@RequestBody SerieBaseDTO serieDTO) {
        return ResponseEntity.ok(serieService.guardarSerie(serieDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSerie(@PathVariable Long id) {
        serieService.eliminarSerie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/aluminio")
    public ResponseEntity<List<SerieAluminioDTO>> obtenerSeriesAluminio() {
        return ResponseEntity.ok(serieService.obtenerSeriesAluminio());
    }

    @GetMapping("/aluminio/tipo/{tipoSerie}")
    public ResponseEntity<List<SerieAluminioDTO>> obtenerSeriesAluminioPorTipo(
            @PathVariable TipoSerie tipoSerie) {
        return ResponseEntity.ok(serieService.obtenerSeriesAluminioPorTipo(tipoSerie));
    }

    @GetMapping("/aluminio/{id}")
    public ResponseEntity<SerieAluminioDTO> obtenerSerieAluminioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(serieService.obtenerSerieAluminioPorId(id));
    }

    @PostMapping("/aluminio")
    public ResponseEntity<SerieAluminioDTO> guardarSerieAluminio(@RequestBody SerieAluminioDTO serieDTO) {
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

        return ResponseEntity.ok(serieService.crearSerieCompleta(
                codigo, nombre, descripcion, tipoSerie, roturaPuente, permitePersiana));
    }

    @GetMapping("/{serieId}/perfiles")
    public ResponseEntity<List<PerfilSerieDTO>> obtenerPerfilesPorSerieId(@PathVariable Long serieId) {
        return ResponseEntity.ok(serieService.obtenerPerfilesPorSerieId(serieId));
    }

    @PostMapping("/perfiles")
    public ResponseEntity<PerfilSerieDTO> guardarPerfilSerie(@RequestBody PerfilSerieDTO perfilDTO) {
        return ResponseEntity.ok(serieService.guardarPerfilSerie(perfilDTO));
    }

    @DeleteMapping("/perfiles/{id}")
    public ResponseEntity<Void> eliminarPerfilSerie(@PathVariable Long id) {
        serieService.eliminarPerfilSerie(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{serieId}/descuentos")
    public ResponseEntity<List<DescuentoPerfilSerieDTO>> obtenerDescuentosPorSerieId(@PathVariable Long serieId) {
        return ResponseEntity.ok(serieService.obtenerDescuentosPorSerieId(serieId));
    }

    @PostMapping("/descuentos")
    public ResponseEntity<DescuentoPerfilSerieDTO> guardarDescuentoPerfilSerie(
            @RequestBody DescuentoPerfilSerieDTO descuentoDTO) {
        return ResponseEntity.ok(serieService.guardarDescuentoPerfilSerie(descuentoDTO));
    }

    @DeleteMapping("/descuentos/{id}")
    public ResponseEntity<Void> eliminarDescuentoPerfilSerie(@PathVariable Long id) {
        serieService.eliminarDescuentoPerfilSerie(id);
        return ResponseEntity.ok().build();
    }
}
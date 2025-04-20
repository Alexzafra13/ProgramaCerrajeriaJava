package com.gestiontaller.server.controller;

import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.server.service.interfaces.ComponenteSerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de componentes de series
 */
@RestController
@RequestMapping("/api/componentes-serie")
public class ComponenteSerieController {

    private final ComponenteSerieService componenteSerieService;

    @Autowired
    public ComponenteSerieController(ComponenteSerieService componenteSerieService) {
        this.componenteSerieService = componenteSerieService;
    }

    /**
     * Obtiene todos los materiales base para una serie
     */
    @GetMapping("/serie/{serieId}")
    public ResponseEntity<List<MaterialBaseSerieDTO>> obtenerComponentesPorSerie(
            @PathVariable("serieId") Long serieId) {
        return ResponseEntity.ok(componenteSerieService.obtenerMaterialesBasePorSerie(serieId));
    }

    /**
     * Obtiene los materiales base de un tipo específico para una serie
     */
    @GetMapping("/serie/{serieId}/tipo/{tipoMaterial}")
    public ResponseEntity<List<MaterialBaseSerieDTO>> obtenerComponentesPorTipo(
            @PathVariable("serieId") Long serieId,
            @PathVariable("tipoMaterial") String tipoMaterial) {
        return ResponseEntity.ok(componenteSerieService.obtenerMaterialesBasePorTipo(serieId, tipoMaterial));
    }

    /**
     * Guarda un material base
     */
    @PostMapping
    public ResponseEntity<MaterialBaseSerieDTO> guardarComponente(@RequestBody MaterialBaseSerieDTO materialDTO) {
        return ResponseEntity.ok(componenteSerieService.guardarMaterialBase(materialDTO));
    }

    /**
     * Elimina un material base
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComponente(@PathVariable("id") Long id) {
        componenteSerieService.eliminarMaterialBase(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Calcula los componentes necesarios para una ventana
     */
    @GetMapping("/calcular")
    public ResponseEntity<Map<String, List<MaterialBaseSerieDTO>>> calcularComponentesVentana(
            @RequestParam("serieId") Long serieId,
            @RequestParam("numHojas") Integer numHojas,
            @RequestParam("ancho") Integer ancho,
            @RequestParam("alto") Integer alto,
            @RequestParam(value = "incluyePersiana", defaultValue = "false") Boolean incluyePersiana) {

        return ResponseEntity.ok(componenteSerieService.calcularComponentesParaVentana(
                serieId, numHojas, ancho, alto, incluyePersiana));
    }

    /**
     * Genera una configuración estándar de componentes para una serie
     */
    @PostMapping("/generar-configuracion-estandar")
    public ResponseEntity<Void> generarConfiguracionEstandar(
            @RequestParam("serieId") Long serieId,
            @RequestParam("tipoSerie") String tipoSerie) {

        componenteSerieService.generarConfiguracionEstandardComponentes(serieId, tipoSerie);
        return ResponseEntity.ok().build();
    }
}
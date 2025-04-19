package com.gestiontaller.server.controller;

import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.server.model.TipoCristal;
import com.gestiontaller.server.service.interfaces.ConfiguracionSerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuraciones-serie")
public class ConfiguracionSerieController {

    private final ConfiguracionSerieService configuracionService;

    @Autowired
    public ConfiguracionSerieController(ConfiguracionSerieService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping("/serie/{serieId}")
    public ResponseEntity<List<PlantillaConfiguracionSerieDTO>> obtenerConfiguracionesPorSerie(
            @PathVariable("serieId") Long serieId) {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionesPorSerie(serieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaConfiguracionSerieDTO> obtenerConfiguracionPorId(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionPorId(id));
    }

    @GetMapping("/serie/{serieId}/hojas/{numHojas}")
    public ResponseEntity<PlantillaConfiguracionSerieDTO> obtenerConfiguracionPorSerieYHojas(
            @PathVariable("serieId") Long serieId, @PathVariable("numHojas") Integer numHojas) {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionPorSerieYHojas(serieId, numHojas));
    }

    @PostMapping
    public ResponseEntity<PlantillaConfiguracionSerieDTO> guardarConfiguracion(
            @RequestBody PlantillaConfiguracionSerieDTO dto) {
        return ResponseEntity.ok(configuracionService.guardarConfiguracion(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConfiguracion(@PathVariable("id") Long id) {
        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{configuracionId}/aplicar")
    public ResponseEntity<ResultadoCalculoDTO> aplicarConfiguracion(
            @PathVariable("configuracionId") Long configuracionId,
            @RequestParam Integer ancho,
            @RequestParam Integer alto,
            @RequestParam(required = false, defaultValue = "false") Boolean incluyePersiana,
            @RequestParam(required = false) Integer alturaCajon,
            @RequestParam(required = false, defaultValue = "SIMPLE") TipoCristal tipoCristal) {

        return ResponseEntity.ok(configuracionService.aplicarConfiguracion(
                configuracionId, ancho, alto, incluyePersiana, alturaCajon, tipoCristal));
    }
}
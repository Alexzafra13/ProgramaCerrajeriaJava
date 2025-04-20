package com.gestiontaller.server.controller;

import com.gestiontaller.common.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.common.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.common.model.ventana.TipoCristal;
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
        System.out.println("Obteniendo configuraciones para serie ID: " + serieId);
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionesPorSerie(serieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaConfiguracionSerieDTO> obtenerConfiguracionPorId(
            @PathVariable("id") Long id) {
        System.out.println("Obteniendo configuración con ID: " + id);
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionPorId(id));
    }

    @GetMapping("/serie/{serieId}/hojas/{numHojas}")
    public ResponseEntity<PlantillaConfiguracionSerieDTO> obtenerConfiguracionPorSerieYHojas(
            @PathVariable("serieId") Long serieId, @PathVariable("numHojas") Integer numHojas) {
        System.out.println("Obteniendo configuración para serie ID: " + serieId + " con " + numHojas + " hojas");
        return ResponseEntity.ok(configuracionService.obtenerConfiguracionPorSerieYHojas(serieId, numHojas));
    }

    @PostMapping
    public ResponseEntity<PlantillaConfiguracionSerieDTO> guardarConfiguracion(
            @RequestBody PlantillaConfiguracionSerieDTO dto) {
        System.out.println("Guardando configuración: " + dto.getNombre());
        return ResponseEntity.ok(configuracionService.guardarConfiguracion(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConfiguracion(@PathVariable("id") Long id) {
        System.out.println("Eliminando configuración ID: " + id);
        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{configuracionId}/aplicar")
    public ResponseEntity<ResultadoCalculoDTO> aplicarConfiguracion(
            @PathVariable("configuracionId") Long configuracionId,
            @RequestParam Integer ancho,
            @RequestParam Integer alto,
            @RequestParam(required = false, defaultValue = "false") Boolean incluyePersiana,
            @RequestParam(required = false) Integer alturaCajon,
            @RequestParam(required = false, defaultValue = "SIMPLE") TipoCristal tipoCristal) {

        System.out.println("Controller: Procesando cálculo para configuración ID: " + configuracionId);
        System.out.println("Controller: Parámetros: ancho=" + ancho + ", alto=" + alto +
                ", incluyePersiana=" + incluyePersiana +
                ", alturaCajon=" + alturaCajon +
                ", tipoCristal=" + tipoCristal);

        ResultadoCalculoDTO resultado = configuracionService.aplicarConfiguracion(
                configuracionId, ancho, alto, incluyePersiana, alturaCajon, tipoCristal);

        System.out.println("Controller: Cálculo completado correctamente");
        return ResponseEntity.ok(resultado);
    }
}
package com.gestiontaller.server.controller;

import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import com.gestiontaller.server.service.interfaces.PresupuestoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    private static final Logger logger = LoggerFactory.getLogger(PresupuestoController.class);

    private final PresupuestoService presupuestoService;

    @Autowired
    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping
    public ResponseEntity<List<PresupuestoDTO>> obtenerTodos() {
        logger.debug("REST - Obteniendo todos los presupuestos");
        return ResponseEntity.ok(presupuestoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDTO> obtenerPorId(@PathVariable Long id) {
        logger.debug("REST - Obteniendo presupuesto con ID: {}", id);
        return ResponseEntity.ok(presupuestoService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<PresupuestoDTO> obtenerPorNumero(@PathVariable String numero) {
        logger.debug("REST - Obteniendo presupuesto con número: {}", numero);
        return ResponseEntity.ok(presupuestoService.obtenerPorNumero(numero));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PresupuestoDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        logger.debug("REST - Obteniendo presupuestos para cliente ID: {}", clienteId);
        return ResponseEntity.ok(presupuestoService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PresupuestoDTO>> obtenerPorEstado(@PathVariable EstadoPresupuesto estado) {
        logger.debug("REST - Obteniendo presupuestos con estado: {}", estado);
        return ResponseEntity.ok(presupuestoService.obtenerPorEstado(estado));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<PresupuestoDTO>> obtenerPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        logger.debug("REST - Obteniendo presupuestos entre fechas: {} y {}", fechaInicio, fechaFin);
        return ResponseEntity.ok(presupuestoService.obtenerPorFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PresupuestoDTO>> buscar(@RequestParam String texto) {
        logger.debug("REST - Buscando presupuestos con texto: {}", texto);
        return ResponseEntity.ok(presupuestoService.buscar(texto));
    }

    @GetMapping("/buscar/estado")
    public ResponseEntity<List<PresupuestoDTO>> buscarPorEstado(
            @RequestParam String texto,
            @RequestParam EstadoPresupuesto estado) {
        logger.debug("REST - Buscando presupuestos con texto: {} y estado: {}", texto, estado);
        return ResponseEntity.ok(presupuestoService.buscarPorEstado(texto, estado));
    }

    @PostMapping
    public ResponseEntity<PresupuestoDTO> guardar(@RequestBody PresupuestoDTO presupuestoDTO) {
        logger.debug("REST - Guardando presupuesto: {}", presupuestoDTO.getNumero());
        return new ResponseEntity<>(presupuestoService.guardar(presupuestoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDTO> actualizar(@PathVariable Long id, @RequestBody PresupuestoDTO presupuestoDTO) {
        logger.debug("REST - Actualizando presupuesto con ID: {}", id);
        presupuestoDTO.setId(id);
        return ResponseEntity.ok(presupuestoService.guardar(presupuestoDTO));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PresupuestoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos) {

        EstadoPresupuesto nuevoEstado = EstadoPresupuesto.valueOf((String) datos.get("estado"));
        String motivoRechazo = (String) datos.get("motivoRechazo");

        logger.debug("REST - Actualizando estado de presupuesto ID: {} a {}", id, nuevoEstado);
        return ResponseEntity.ok(presupuestoService.actualizarEstado(id, nuevoEstado, motivoRechazo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.debug("REST - Eliminando presupuesto con ID: {}", id);
        presupuestoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{presupuestoId}/lineas")
    public ResponseEntity<LineaPresupuestoDTO> agregarLinea(
            @PathVariable Long presupuestoId,
            @RequestBody LineaPresupuestoDTO lineaDTO) {

        logger.debug("REST - Agregando línea a presupuesto ID: {}", presupuestoId);
        return new ResponseEntity<>(presupuestoService.agregarLinea(presupuestoId, lineaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/lineas/{lineaId}")
    public ResponseEntity<LineaPresupuestoDTO> actualizarLinea(
            @PathVariable Long lineaId,
            @RequestBody LineaPresupuestoDTO lineaDTO) {

        logger.debug("REST - Actualizando línea ID: {}", lineaId);
        lineaDTO.setId(lineaId);
        return ResponseEntity.ok(presupuestoService.actualizarLinea(lineaDTO));
    }

    @DeleteMapping("/lineas/{lineaId}")
    public ResponseEntity<Void> eliminarLinea(@PathVariable Long lineaId) {
        logger.debug("REST - Eliminando línea ID: {}", lineaId);
        presupuestoService.eliminarLinea(lineaId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/recalcular")
    public ResponseEntity<PresupuestoDTO> recalcularTotales(@PathVariable Long id) {
        logger.debug("REST - Recalculando totales de presupuesto ID: {}", id);
        return ResponseEntity.ok(presupuestoService.recalcularTotales(id));
    }

    @GetMapping("/generar-numero")
    public ResponseEntity<String> generarNumeroPresupuesto() {
        logger.debug("REST - Generando número de presupuesto");
        return ResponseEntity.ok(presupuestoService.generarNumeroPresupuesto());
    }
}
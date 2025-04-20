package com.gestiontaller.server.controller;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;
import com.gestiontaller.server.service.interfaces.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodos() {
        logger.debug("REST - Obteniendo todos los clientes");
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        logger.debug("REST - Obteniendo cliente con ID: {}", id);
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ClienteDTO> obtenerPorCodigo(@PathVariable String codigo) {
        logger.debug("REST - Obteniendo cliente con código: {}", codigo);
        return ResponseEntity.ok(clienteService.obtenerPorCodigo(codigo));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ClienteDTO>> obtenerPorTipo(@PathVariable TipoCliente tipo) {
        logger.debug("REST - Obteniendo clientes de tipo: {}", tipo);
        return ResponseEntity.ok(clienteService.obtenerPorTipo(tipo));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscar(@RequestParam String texto) {
        logger.debug("REST - Buscando clientes con texto: {}", texto);
        return ResponseEntity.ok(clienteService.buscar(texto));
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> guardar(@RequestBody ClienteDTO clienteDTO) {
        logger.debug("REST - Guardando cliente: {}", clienteDTO.getCodigo());
        return new ResponseEntity<>(clienteService.guardar(clienteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        logger.debug("REST - Actualizando cliente con ID: {}", id);
        clienteDTO.setId(id);
        return ResponseEntity.ok(clienteService.guardar(clienteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.debug("REST - Eliminando cliente con ID: {}", id);
        clienteService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generar-codigo")
    public ResponseEntity<String> generarCodigo(@RequestParam TipoCliente tipo) {
        logger.debug("REST - Generando código para cliente de tipo: {}", tipo);
        return ResponseEntity.ok(clienteService.generarCodigo(tipo));
    }
}
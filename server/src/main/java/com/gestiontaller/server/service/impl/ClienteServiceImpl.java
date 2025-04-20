package com.gestiontaller.server.service.impl;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;
import com.gestiontaller.server.exception.ResourceNotFoundException;
import com.gestiontaller.server.mapper.ClienteMapper;
import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.repository.cliente.ClienteRepository;
import com.gestiontaller.server.service.interfaces.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        logger.debug("Obteniendo cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorCodigo(String codigo) {
        logger.debug("Obteniendo cliente por código: {}", codigo);
        return clienteRepository.findByCodigo(codigo)
                .map(clienteMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con código: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerPorTipo(TipoCliente tipoCliente) {
        logger.debug("Obteniendo clientes por tipo: {}", tipoCliente);
        return clienteRepository.findByTipoCliente(tipoCliente).stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscar(String texto) {
        logger.debug("Buscando clientes con texto: {}", texto);
        return clienteRepository.buscarPorTexto(texto).stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteDTO guardar(ClienteDTO clienteDTO) {
        logger.debug("Guardando cliente: {}", clienteDTO.getCodigo());

        Cliente cliente;

        if (clienteDTO.getId() != null) {
            // Actualización de cliente existente
            cliente = clienteRepository.findById(clienteDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteDTO.getId()));
            clienteMapper.updateEntityFromDto(clienteDTO, cliente);
        } else {
            // Nuevo cliente
            cliente = clienteMapper.toEntity(clienteDTO);

            // Generar código si no tiene
            if (cliente.getCodigo() == null || cliente.getCodigo().trim().isEmpty()) {
                cliente.setCodigo(generarCodigo(cliente.getTipoCliente()));
            }

            // Establecer fecha de alta si no tiene
            if (cliente.getFechaAlta() == null) {
                cliente.setFechaAlta(LocalDate.now());
            }

            // Por defecto el cliente está activo
            cliente.setActivo(true);
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.toDto(clienteGuardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        logger.debug("Eliminando cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        // En lugar de eliminar físicamente, marcamos como inactivo
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Override
    public String generarCodigo(TipoCliente tipoCliente) {
        logger.debug("Generando código para cliente de tipo: {}", tipoCliente);

        String prefijo;
        switch (tipoCliente) {
            case EMPRESA:
                prefijo = "EMP";
                break;
            case AUTONOMO:
                prefijo = "AUT";
                break;
            case ADMINISTRACION:
                prefijo = "ADM";
                break;
            case PARTICULAR:
            default:
                prefijo = "CLI";
                break;
        }

        // Buscar último código con este prefijo
        List<Cliente> clientes = clienteRepository.findAll();

        int ultimoNumero = 0;
        String patronRegex = prefijo + "\\d+";

        for (Cliente cliente : clientes) {
            String codigo = cliente.getCodigo();
            if (codigo != null && codigo.matches(patronRegex)) {
                try {
                    int numero = Integer.parseInt(codigo.substring(prefijo.length()));
                    if (numero > ultimoNumero) {
                        ultimoNumero = numero;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar códigos que no siguen el formato esperado
                }
            }
        }

        // Incrementar e formatear con ceros a la izquierda (3 dígitos mínimo)
        return String.format("%s%03d", prefijo, ultimoNumero + 1);
    }
}
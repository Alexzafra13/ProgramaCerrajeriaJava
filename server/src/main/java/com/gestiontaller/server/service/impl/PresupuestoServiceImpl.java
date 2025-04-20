package com.gestiontaller.server.service.impl;

import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import com.gestiontaller.server.exception.ResourceNotFoundException;
import com.gestiontaller.server.mapper.LineaPresupuestoMapper;
import com.gestiontaller.server.mapper.PresupuestoMapper;
import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.usuario.Usuario;
import com.gestiontaller.server.repository.cliente.ClienteRepository;
import com.gestiontaller.server.repository.presupuesto.LineaPresupuestoRepository;
import com.gestiontaller.server.repository.presupuesto.PresupuestoRepository;
import com.gestiontaller.server.repository.usuario.UsuarioRepository;
import com.gestiontaller.server.service.interfaces.PresupuestoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresupuestoServiceImpl implements PresupuestoService {

    private static final Logger logger = LoggerFactory.getLogger(PresupuestoServiceImpl.class);

    private final PresupuestoRepository presupuestoRepository;
    private final LineaPresupuestoRepository lineaPresupuestoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PresupuestoMapper presupuestoMapper;
    private final LineaPresupuestoMapper lineaPresupuestoMapper;

    @Autowired
    public PresupuestoServiceImpl(
            PresupuestoRepository presupuestoRepository,
            LineaPresupuestoRepository lineaPresupuestoRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PresupuestoMapper presupuestoMapper,
            LineaPresupuestoMapper lineaPresupuestoMapper) {
        this.presupuestoRepository = presupuestoRepository;
        this.lineaPresupuestoRepository = lineaPresupuestoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.presupuestoMapper = presupuestoMapper;
        this.lineaPresupuestoMapper = lineaPresupuestoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> obtenerTodos() {
        logger.debug("Obteniendo todos los presupuestos");
        return presupuestoRepository.findAll().stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PresupuestoDTO obtenerPorId(Long id) {
        logger.debug("Obteniendo presupuesto por ID: {}", id);
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + id));

        // Asegurarse de que se cargan las líneas
        presupuesto.getLineas().size(); // Fuerza carga de la colección lazy

        return presupuestoMapper.toDto(presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public PresupuestoDTO obtenerPorNumero(String numero) {
        logger.debug("Obteniendo presupuesto por número: {}", numero);
        Presupuesto presupuesto = presupuestoRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con número: " + numero));

        // Asegurarse de que se cargan las líneas
        presupuesto.getLineas().size(); // Fuerza carga de la colección lazy

        return presupuestoMapper.toDto(presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> obtenerPorCliente(Long clienteId) {
        logger.debug("Obteniendo presupuestos por cliente ID: {}", clienteId);

        // Verificar que el cliente existe
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        return presupuestoRepository.findByClienteId(clienteId).stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> obtenerPorEstado(EstadoPresupuesto estado) {
        logger.debug("Obteniendo presupuestos por estado: {}", estado);
        return presupuestoRepository.findByEstado(estado).stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> obtenerPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        logger.debug("Obteniendo presupuestos entre fechas: {} y {}", fechaInicio, fechaFin);
        return presupuestoRepository.findByFechaCreacionBetween(fechaInicio, fechaFin).stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> buscar(String texto) {
        logger.debug("Buscando presupuestos con texto: {}", texto);
        return presupuestoRepository.buscar(texto).stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDTO> buscarPorEstado(String texto, EstadoPresupuesto estado) {
        logger.debug("Buscando presupuestos con texto: {} y estado: {}", texto, estado);
        return presupuestoRepository.buscarPorEstado(texto, estado).stream()
                .map(presupuestoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PresupuestoDTO guardar(PresupuestoDTO presupuestoDTO) {
        logger.debug("Guardando presupuesto: {}", presupuestoDTO.getNumero());

        Presupuesto presupuesto;
        boolean esNuevo = false;

        if (presupuestoDTO.getId() != null) {
            // Actualización de presupuesto existente
            presupuesto = presupuestoRepository.findById(presupuestoDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + presupuestoDTO.getId()));

            // Si cambia el cliente, verificar que existe
            if (!presupuesto.getCliente().getId().equals(presupuestoDTO.getClienteId())) {
                Cliente nuevoCliente = clienteRepository.findById(presupuestoDTO.getClienteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + presupuestoDTO.getClienteId()));
                presupuesto.setCliente(nuevoCliente);
            }

            // Actualizar campos del presupuesto
            presupuesto.setFechaValidez(presupuestoDTO.getFechaValidez());
            presupuesto.setObservaciones(presupuestoDTO.getObservaciones());
            presupuesto.setDireccionObra(presupuestoDTO.getDireccionObra());
            presupuesto.setReferencia(presupuestoDTO.getReferencia());
            presupuesto.setDescuento(presupuestoDTO.getDescuento());
            presupuesto.setTiempoEstimado(presupuestoDTO.getTiempoEstimado());

            // El estado se cambia con actualizarEstado
            if (presupuestoDTO.getEstado() != presupuesto.getEstado()) {
                presupuesto.setEstado(presupuestoDTO.getEstado());
                if (presupuestoDTO.getEstado() == EstadoPresupuesto.RECHAZADO) {
                    presupuesto.setMotivoRechazo(presupuestoDTO.getMotivoRechazo());
                }
            }
        } else {
            // Nuevo presupuesto
            esNuevo = true;
            presupuesto = new Presupuesto();

            // Buscar el cliente
            Cliente cliente = clienteRepository.findById(presupuestoDTO.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + presupuestoDTO.getClienteId()));
            presupuesto.setCliente(cliente);

            // Buscar el usuario
            Usuario usuario = usuarioRepository.findById(presupuestoDTO.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + presupuestoDTO.getUsuarioId()));
            presupuesto.setUsuario(usuario);

            // Generar número de presupuesto si no tiene
            if (presupuestoDTO.getNumero() == null || presupuestoDTO.getNumero().trim().isEmpty()) {
                presupuesto.setNumero(generarNumeroPresupuesto());
            } else {
                presupuesto.setNumero(presupuestoDTO.getNumero());
            }

            // Establecer fecha de creación
            presupuesto.setFechaCreacion(LocalDateTime.now());

            // Configurar otros campos
            presupuesto.setFechaValidez(presupuestoDTO.getFechaValidez() != null ?
                    presupuestoDTO.getFechaValidez() : LocalDate.now().plusMonths(1));
            presupuesto.setEstado(EstadoPresupuesto.PENDIENTE);
            presupuesto.setObservaciones(presupuestoDTO.getObservaciones());
            presupuesto.setBaseImponible(0.0);
            presupuesto.setImporteIva(0.0);
            presupuesto.setTotalPresupuesto(0.0);
            presupuesto.setDescuento(presupuestoDTO.getDescuento());
            presupuesto.setTiempoEstimado(presupuestoDTO.getTiempoEstimado());
            presupuesto.setDireccionObra(presupuestoDTO.getDireccionObra());
            presupuesto.setReferencia(presupuestoDTO.getReferencia());
        }

        // Guardar el presupuesto
        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);

        // Si es nuevo y tiene líneas, guardarlas
        if (esNuevo && presupuestoDTO.getLineas() != null && !presupuestoDTO.getLineas().isEmpty()) {
            for (LineaPresupuestoDTO lineaDTO : presupuestoDTO.getLineas()) {
                lineaDTO.setPresupuestoId(presupuestoGuardado.getId());
                LineaPresupuesto linea = lineaPresupuestoMapper.toEntity(lineaDTO);
                linea.setPresupuesto(presupuestoGuardado);
                lineaPresupuestoRepository.save(linea);
            }

            // Recalcular totales
            recalcularTotales(presupuestoGuardado);
            presupuestoGuardado = presupuestoRepository.save(presupuestoGuardado);
        }

        // Si no es nuevo pero hay líneas en el DTO, actualizar las líneas
        if (!esNuevo && presupuestoDTO.getLineas() != null) {
            // En este caso, simplemente recalculamos los totales
            // Las líneas se gestionan por separado con los métodos específicos
            recalcularTotales(presupuestoGuardado);
            presupuestoGuardado = presupuestoRepository.save(presupuestoGuardado);
        }

        return presupuestoMapper.toDto(presupuestoGuardado);
    }

    @Override
    @Transactional
    public PresupuestoDTO actualizarEstado(Long id, EstadoPresupuesto nuevoEstado, String motivoRechazo) {
        logger.debug("Actualizando estado de presupuesto ID: {} a {}", id, nuevoEstado);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + id));

        // Validar transición de estado
        validarTransicionEstado(presupuesto.getEstado(), nuevoEstado);

        // Actualizar estado
        presupuesto.setEstado(nuevoEstado);

        // Si es rechazado, guardar motivo
        if (nuevoEstado == EstadoPresupuesto.RECHAZADO) {
            presupuesto.setMotivoRechazo(motivoRechazo);
        }

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);
        return presupuestoMapper.toDto(presupuestoGuardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        logger.debug("Eliminando presupuesto con ID: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + id));

        // En lugar de eliminar físicamente, marcar como CANCELADO
        presupuesto.setEstado(EstadoPresupuesto.CANCELADO);
        presupuestoRepository.save(presupuesto);
    }

    @Override
    @Transactional
    public LineaPresupuestoDTO agregarLinea(Long presupuestoId, LineaPresupuestoDTO lineaDTO) {
        logger.debug("Agregando línea a presupuesto ID: {}", presupuestoId);

        Presupuesto presupuesto = presupuestoRepository.findById(presupuestoId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + presupuestoId));

        // Determinar el orden de la nueva línea
        int nuevoOrden = presupuesto.getLineas().size() + 1;

        // Crear nueva línea
        LineaPresupuesto linea = lineaPresupuestoMapper.toEntity(lineaDTO);
        linea.setPresupuesto(presupuesto);
        linea.setOrden(nuevoOrden);

        // Si no tiene cantidad, establecer 1
        if (linea.getCantidad() == null || linea.getCantidad() <= 0) {
            linea.setCantidad(1);
        }

        // Calcular importe si tiene precio
        if (linea.getPrecioUnitario() > 0) {
            double descuentoLinea = linea.getDescuento() / 100.0; // Descuento en porcentaje
            linea.setImporte(linea.getPrecioUnitario() * linea.getCantidad() * (1 - descuentoLinea));
        }

        LineaPresupuesto lineaGuardada = lineaPresupuestoRepository.save(linea);

        // Recalcular totales del presupuesto
        recalcularTotales(presupuesto);
        presupuestoRepository.save(presupuesto);

        return lineaPresupuestoMapper.toDto(lineaGuardada);
    }

    @Override
    @Transactional
    public LineaPresupuestoDTO actualizarLinea(LineaPresupuestoDTO lineaDTO) {
        logger.debug("Actualizando línea de presupuesto ID: {}", lineaDTO.getId());

        LineaPresupuesto linea = lineaPresupuestoRepository.findById(lineaDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Línea de presupuesto no encontrada con ID: " + lineaDTO.getId()));

        // Actualizar campos de la línea
        lineaPresupuestoMapper.updateEntityFromDto(lineaDTO, linea);

        // Calcular importe
        if (linea.getPrecioUnitario() > 0) {
            double descuentoLinea = linea.getDescuento() / 100.0; // Descuento en porcentaje
            linea.setImporte(linea.getPrecioUnitario() * linea.getCantidad() * (1 - descuentoLinea));
        }

        LineaPresupuesto lineaGuardada = lineaPresupuestoRepository.save(linea);

        // Recalcular totales del presupuesto
        recalcularTotales(linea.getPresupuesto());
        presupuestoRepository.save(linea.getPresupuesto());

        return lineaPresupuestoMapper.toDto(lineaGuardada);
    }

    @Override
    @Transactional
    public void eliminarLinea(Long lineaId) {
        logger.debug("Eliminando línea de presupuesto ID: {}", lineaId);

        LineaPresupuesto linea = lineaPresupuestoRepository.findById(lineaId)
                .orElseThrow(() -> new ResourceNotFoundException("Línea de presupuesto no encontrada con ID: " + lineaId));

        Presupuesto presupuesto = linea.getPresupuesto();

        // Eliminar la línea
        lineaPresupuestoRepository.delete(linea);

        // Reordenar las líneas restantes
        List<LineaPresupuesto> lineasRestantes = lineaPresupuestoRepository.findByPresupuestoIdOrderByOrden(presupuesto.getId());
        for (int i = 0; i < lineasRestantes.size(); i++) {
            LineaPresupuesto l = lineasRestantes.get(i);
            l.setOrden(i + 1);
            lineaPresupuestoRepository.save(l);
        }

        // Recalcular totales del presupuesto
        recalcularTotales(presupuesto);
        presupuestoRepository.save(presupuesto);
    }

    @Override
    @Transactional
    public PresupuestoDTO recalcularTotales(Long presupuestoId) {
        logger.debug("Recalculando totales de presupuesto ID: {}", presupuestoId);

        Presupuesto presupuesto = presupuestoRepository.findById(presupuestoId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado con ID: " + presupuestoId));

        return presupuestoMapper.toDto(recalcularTotales(presupuesto));
    }

    private Presupuesto recalcularTotales(Presupuesto presupuesto) {
        double baseImponible = 0.0;
        double tipoIVA = 0.21; // 21% IVA por defecto

        // Sumar importes de todas las líneas
        for (LineaPresupuesto linea : presupuesto.getLineas()) {
            baseImponible += linea.getImporte();
        }

        // Aplicar descuento general
        if (presupuesto.getDescuento() > 0) {
            double descuentoGeneral = presupuesto.getDescuento() / 100.0; // Descuento en porcentaje
            baseImponible = baseImponible * (1 - descuentoGeneral);
        }

        // Calcular IVA e importe total
        double importeIVA = baseImponible * tipoIVA;
        double total = baseImponible + importeIVA;

        // Actualizar presupuesto
        presupuesto.setBaseImponible(baseImponible);
        presupuesto.setImporteIva(importeIVA);
        presupuesto.setTotalPresupuesto(total);

        return presupuesto;
    }

    @Override
    public String generarNumeroPresupuesto() {
        // Formato: PRES-YYYY-NNNN donde YYYY es el año actual y NNNN es un número secuencial
        String prefijo = "PRES-" + Calendar.getInstance().get(Calendar.YEAR) + "-";

        // Buscar el último número con este prefijo
        Integer ultimoNumero = presupuestoRepository.findLastNumeroByPrefijo(prefijo);

        // Si no hay ninguno, empezar desde 1
        int nuevoNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;

        // Formatear con ceros a la izquierda (4 dígitos mínimo)
        return String.format("%s%04d", prefijo, nuevoNumero);
    }

    private void validarTransicionEstado(EstadoPresupuesto estadoActual, EstadoPresupuesto nuevoEstado) {
        // Verificar que la transición de estado sea válida
        // Por ejemplo, no se puede pasar de CANCELADO a PENDIENTE

        if (estadoActual == EstadoPresupuesto.CANCELADO && nuevoEstado != EstadoPresupuesto.CANCELADO) {
            throw new IllegalStateException("No se puede cambiar el estado de un presupuesto cancelado");
        }

        if (estadoActual == EstadoPresupuesto.FACTURADO && nuevoEstado != EstadoPresupuesto.FACTURADO) {
            throw new IllegalStateException("No se puede cambiar el estado de un presupuesto facturado");
        }

        // Otras reglas de transición pueden añadirse aquí
    }
}
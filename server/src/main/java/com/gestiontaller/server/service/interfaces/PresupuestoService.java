package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;

import java.time.LocalDate;
import java.util.List;

public interface PresupuestoService {

    /**
     * Obtiene todos los presupuestos
     * @return Lista de presupuestos
     */
    List<PresupuestoDTO> obtenerTodos();

    /**
     * Obtiene un presupuesto por su ID
     * @param id ID del presupuesto
     * @return El presupuesto encontrado
     */
    PresupuestoDTO obtenerPorId(Long id);

    /**
     * Obtiene un presupuesto por su número
     * @param numero Número del presupuesto
     * @return El presupuesto encontrado
     */
    PresupuestoDTO obtenerPorNumero(String numero);

    /**
     * Obtiene presupuestos por cliente
     * @param clienteId ID del cliente
     * @return Lista de presupuestos del cliente
     */
    List<PresupuestoDTO> obtenerPorCliente(Long clienteId);

    /**
     * Obtiene presupuestos por estado
     * @param estado Estado del presupuesto
     * @return Lista de presupuestos en el estado especificado
     */
    List<PresupuestoDTO> obtenerPorEstado(EstadoPresupuesto estado);

    /**
     * Obtiene presupuestos entre dos fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de presupuestos creados entre las fechas
     */
    List<PresupuestoDTO> obtenerPorFechas(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca presupuestos que coincidan con el texto en diferentes campos
     * @param texto Texto a buscar
     * @return Lista de presupuestos que coinciden
     */
    List<PresupuestoDTO> buscar(String texto);

    /**
     * Busca presupuestos por texto y estado
     * @param texto Texto a buscar
     * @param estado Estado del presupuesto
     * @return Lista de presupuestos que coinciden
     */
    List<PresupuestoDTO> buscarPorEstado(String texto, EstadoPresupuesto estado);

    /**
     * Guarda un presupuesto (crea uno nuevo o actualiza uno existente)
     * @param presupuestoDTO Presupuesto a guardar
     * @return Presupuesto guardado
     */
    PresupuestoDTO guardar(PresupuestoDTO presupuestoDTO);

    /**
     * Actualiza el estado de un presupuesto
     * @param id ID del presupuesto
     * @param nuevoEstado Nuevo estado
     * @param motivoRechazo Motivo de rechazo (solo si el estado es RECHAZADO)
     * @return Presupuesto actualizado
     */
    PresupuestoDTO actualizarEstado(Long id, EstadoPresupuesto nuevoEstado, String motivoRechazo);

    /**
     * Elimina un presupuesto (lo marca como CANCELADO)
     * @param id ID del presupuesto a eliminar
     */
    void eliminar(Long id);

    /**
     * Añade una línea a un presupuesto
     * @param presupuestoId ID del presupuesto
     * @param lineaDTO Línea a añadir
     * @return La línea añadida
     */
    LineaPresupuestoDTO agregarLinea(Long presupuestoId, LineaPresupuestoDTO lineaDTO);

    /**
     * Actualiza una línea de presupuesto
     * @param lineaDTO Línea a actualizar
     * @return La línea actualizada
     */
    LineaPresupuestoDTO actualizarLinea(LineaPresupuestoDTO lineaDTO);

    /**
     * Elimina una línea de presupuesto
     * @param lineaId ID de la línea a eliminar
     */
    void eliminarLinea(Long lineaId);

    /**
     * Recalcula los totales de un presupuesto
     * @param presupuestoId ID del presupuesto
     * @return Presupuesto actualizado
     */
    PresupuestoDTO recalcularTotales(Long presupuestoId);

    /**
     * Genera un nuevo número de presupuesto
     * @return Número generado
     */
    String generarNumeroPresupuesto();
}
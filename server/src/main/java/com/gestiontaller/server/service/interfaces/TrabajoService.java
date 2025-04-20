package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.trabajo.CambioEstadoDTO;
import com.gestiontaller.common.dto.trabajo.MaterialAsignadoDTO;
import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;

import java.time.LocalDate;
import java.util.List;

public interface TrabajoService {

    /**
     * Obtiene todos los trabajos
     * @return Lista de trabajos
     */
    List<TrabajoDTO> obtenerTodos();

    /**
     * Obtiene un trabajo por su ID
     * @param id ID del trabajo
     * @return El trabajo encontrado
     */
    TrabajoDTO obtenerPorId(Long id);

    /**
     * Obtiene un trabajo por su código
     * @param codigo Código del trabajo
     * @return El trabajo encontrado
     */
    TrabajoDTO obtenerPorCodigo(String codigo);

    /**
     * Obtiene trabajos por cliente
     * @param clienteId ID del cliente
     * @return Lista de trabajos del cliente
     */
    List<TrabajoDTO> obtenerPorCliente(Long clienteId);

    /**
     * Obtiene trabajos por presupuesto
     * @param presupuestoId ID del presupuesto
     * @return Lista de trabajos del presupuesto
     */
    List<TrabajoDTO> obtenerPorPresupuesto(Long presupuestoId);

    /**
     * Obtiene trabajos por usuario asignado
     * @param usuarioId ID del usuario
     * @return Lista de trabajos asignados al usuario
     */
    List<TrabajoDTO> obtenerPorUsuarioAsignado(Long usuarioId);

    /**
     * Obtiene trabajos por estado
     * @param estadoId ID del estado
     * @return Lista de trabajos en el estado especificado
     */
    List<TrabajoDTO> obtenerPorEstado(Long estadoId);

    /**
     * Obtiene trabajos por código de estado
     * @param codigoEstado Código del estado
     * @return Lista de trabajos en el estado especificado
     */
    List<TrabajoDTO> obtenerPorCodigoEstado(String codigoEstado);

    /**
     * Obtiene trabajos programados para una fecha
     * @param fecha Fecha programada
     * @return Lista de trabajos para la fecha
     */
    List<TrabajoDTO> obtenerPorFechaProgramada(LocalDate fecha);

    /**
     * Obtiene trabajos programados entre dos fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de trabajos entre las fechas
     */
    List<TrabajoDTO> obtenerPorFechasProgramadas(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca trabajos que coincidan con el texto en diferentes campos
     * @param texto Texto a buscar
     * @return Lista de trabajos que coinciden
     */
    List<TrabajoDTO> buscar(String texto);

    /**
     * Busca trabajos por texto y estado
     * @param texto Texto a buscar
     * @param estadoId ID del estado
     * @return Lista de trabajos que coinciden
     */
    List<TrabajoDTO> buscarPorEstado(String texto, Long estadoId);

    /**
     * Guarda un trabajo (crea uno nuevo o actualiza uno existente)
     * @param trabajoDTO Trabajo a guardar
     * @return Trabajo guardado
     */
    TrabajoDTO guardar(TrabajoDTO trabajoDTO);

    /**
     * Crea un trabajo a partir de un presupuesto
     * @param presupuestoId ID del presupuesto
     * @return Trabajo creado
     */
    TrabajoDTO crearDesdePrespuesto(Long presupuestoId);

    /**
     * Cambia el estado de un trabajo
     * @param id ID del trabajo
     * @param estadoId ID del nuevo estado
     * @param observaciones Observaciones del cambio
     * @param motivoCambio Motivo del cambio
     * @param usuarioId ID del usuario que realiza el cambio
     * @return Trabajo actualizado
     */
    TrabajoDTO cambiarEstado(Long id, Long estadoId, String observaciones, String motivoCambio, Long usuarioId);

    /**
     * Actualiza la prioridad de un trabajo
     * @param id ID del trabajo
     * @param prioridad Nueva prioridad
     * @return Trabajo actualizado
     */
    TrabajoDTO actualizarPrioridad(Long id, PrioridadTrabajo prioridad);

    /**
     * Asigna un usuario a un trabajo
     * @param id ID del trabajo
     * @param usuarioId ID del usuario
     * @return Trabajo actualizado
     */
    TrabajoDTO asignarUsuario(Long id, Long usuarioId);

    /**
     * Programa un trabajo para una fecha
     * @param id ID del trabajo
     * @param fecha Fecha programada
     * @return Trabajo actualizado
     */
    TrabajoDTO programarFecha(Long id, LocalDate fecha);

    /**
     * Registra inicio de trabajo
     * @param id ID del trabajo
     * @param usuarioId ID del usuario que inicia
     * @return Trabajo actualizado
     */
    TrabajoDTO iniciarTrabajo(Long id, Long usuarioId);

    /**
     * Registra finalización de trabajo
     * @param id ID del trabajo
     * @param usuarioId ID del usuario que finaliza
     * @param observaciones Observaciones de finalización
     * @return Trabajo actualizado
     */
    TrabajoDTO finalizarTrabajo(Long id, Long usuarioId, String observaciones);

    /**
     * Obtiene el historial de cambios de estado de un trabajo
     * @param trabajoId ID del trabajo
     * @return Lista de cambios de estado
     */
    List<CambioEstadoDTO> obtenerHistorialCambios(Long trabajoId);

    /**
     * Añade un material a un trabajo
     * @param trabajoId ID del trabajo
     * @param materialDTO Material a añadir
     * @return El material añadido
     */
    MaterialAsignadoDTO agregarMaterial(Long trabajoId, MaterialAsignadoDTO materialDTO);

    /**
     * Actualiza un material asignado
     * @param materialDTO Material a actualizar
     * @return El material actualizado
     */
    MaterialAsignadoDTO actualizarMaterial(MaterialAsignadoDTO materialDTO);

    /**
     * Elimina un material asignado
     * @param materialId ID del material a eliminar
     */
    void eliminarMaterial(Long materialId);

    /**
     * Obtiene los materiales asignados a un trabajo
     * @param trabajoId ID del trabajo
     * @return Lista de materiales asignados
     */
    List<MaterialAsignadoDTO> obtenerMaterialesAsignados(Long trabajoId);

    /**
     * Genera un nuevo código de trabajo
     * @return Código generado
     */
    String generarCodigoTrabajo();
}
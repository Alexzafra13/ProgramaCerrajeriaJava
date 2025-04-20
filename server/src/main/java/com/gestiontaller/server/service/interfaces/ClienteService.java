package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;

import java.util.List;

public interface ClienteService {

    /**
     * Obtiene todos los clientes
     * @return Lista de clientes
     */
    List<ClienteDTO> obtenerTodos();

    /**
     * Obtiene un cliente por su ID
     * @param id ID del cliente
     * @return El cliente encontrado
     */
    ClienteDTO obtenerPorId(Long id);

    /**
     * Obtiene un cliente por su código
     * @param codigo Código del cliente
     * @return El cliente encontrado
     */
    ClienteDTO obtenerPorCodigo(String codigo);

    /**
     * Obtiene clientes por tipo
     * @param tipoCliente Tipo de cliente
     * @return Lista de clientes del tipo especificado
     */
    List<ClienteDTO> obtenerPorTipo(TipoCliente tipoCliente);

    /**
     * Busca clientes que coincidan con el texto en diferentes campos
     * @param texto Texto a buscar
     * @return Lista de clientes que coinciden
     */
    List<ClienteDTO> buscar(String texto);

    /**
     * Guarda un cliente (crea uno nuevo o actualiza uno existente)
     * @param clienteDTO Cliente a guardar
     * @return Cliente guardado
     */
    ClienteDTO guardar(ClienteDTO clienteDTO);

    /**
     * Elimina un cliente
     * @param id ID del cliente a eliminar
     */
    void eliminar(Long id);

    /**
     * Genera un nuevo código de cliente
     * @param tipoCliente Tipo de cliente
     * @return Código generado
     */
    String generarCodigo(TipoCliente tipoCliente);
}
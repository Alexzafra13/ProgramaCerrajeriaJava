package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.usuario.UsuarioDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioDTO obtenerUsuarioPorId(Long id);

    UsuarioDTO obtenerUsuarioPorUsername(String username);

    List<UsuarioDTO> obtenerTodosLosUsuarios();

    List<UsuarioDTO> obtenerUsuariosPorRol(Rol rol);

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO);

    void eliminarUsuario(Long id);

    boolean autenticar(String username, String password);

    void actualizarUltimoAcceso(String username);

    UsuarioDTO obtenerUsuarioActual();

    boolean cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword);
}
package com.gestiontaller.server.service.impl;

import com.gestiontaller.common.dto.usuario.UsuarioDTO;
import com.gestiontaller.common.model.usuario.Rol;
import com.gestiontaller.server.mapper.UsuarioMapper;
import com.gestiontaller.server.model.usuario.Usuario;
import com.gestiontaller.server.repository.usuario.UsuarioRepository;
import com.gestiontaller.server.service.interfaces.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // Usuario actual simulado (en una implementación completa se usaría Spring Security)
    private Usuario usuarioActual;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(guardado);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si se está cambiando el username y si ya existe
        if (!usuario.getUsername().equals(usuarioDTO.getUsername()) &&
                usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Actualizar los datos modificables
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellidos(usuarioDTO.getApellidos());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setActivo(usuarioDTO.isActivo());
        usuario.setPreferenciasVisuales(usuarioDTO.getPreferenciasVisuales());

        // Si se proporcionó una nueva contraseña, actualizarla
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(guardado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean autenticar(String username, String password) {
        System.out.println("==== Inicio de autenticación ====");
        System.out.println("Usuario: " + username);

        try {
            // SOLUCIÓN TEMPORAL - SOLO PARA PRUEBAS
            if ("admin".equals(username) && "admin".equals(password)) {
                System.out.println("¡Autenticación de prueba exitosa!");

                // Actualizar último acceso en manera segura
                try {
                    Optional<Usuario> usuarioTmp = usuarioRepository.findByUsername(username);
                    if (usuarioTmp.isPresent()) {
                        Usuario usuario = usuarioTmp.get();
                        usuario.setUltimoAcceso(LocalDateTime.now());
                        usuarioRepository.save(usuario);
                    }
                } catch (Exception ex) {
                    System.err.println("Error al actualizar último acceso: " + ex.getMessage());
                    // No interrumpir la autenticación si esto falla
                }

                return true;
            }

            // Código normal de autenticación
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                System.out.println("Usuario encontrado en BD: " + usuario.getUsername());
                System.out.println("Hash en BD: " + usuario.getPassword());

                boolean matches = passwordEncoder.matches(password, usuario.getPassword());
                System.out.println("¿Contraseñas coinciden?: " + matches);

                if (matches) {
                    try {
                        usuario.setUltimoAcceso(LocalDateTime.now());
                        usuarioRepository.save(usuario);
                    } catch (Exception e) {
                        System.err.println("Error al actualizar último acceso: " + e.getMessage());
                    }
                }

                return matches;
            } else {
                System.out.println("Usuario no encontrado");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void actualizarUltimoAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioActual() {
        if (this.usuarioActual == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }
        return usuarioMapper.toDto(this.usuarioActual);
    }

    @Override
    @Transactional
    public boolean cambiarPassword(Long usuarioId, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }
}
package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.auth.LoginRequest;
import com.gestiontaller.server.dto.auth.LoginResponse;
import com.gestiontaller.server.model.usuario.Usuario;
import com.gestiontaller.server.repository.usuario.UsuarioRepository;
import com.gestiontaller.server.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Iniciando login para usuario: " + loginRequest.getUsername());

        // SOLUCIÓN TEMPORAL - SOLO PARA PRUEBAS
        if ("admin".equals(loginRequest.getUsername()) && "admin".equals(loginRequest.getPassword())) {
            System.out.println("¡Autenticación de prueba exitosa en AuthService!");

            // Actualizar último acceso en manera segura
            try {
                Optional<Usuario> usuarioTmp = usuarioRepository.findByUsername(loginRequest.getUsername());
                if (usuarioTmp.isPresent()) {
                    Usuario usuario = usuarioTmp.get();
                    usuario.setUltimoAcceso(LocalDateTime.now());
                    usuarioRepository.save(usuario);

                    // Crear token simple
                    String token = UUID.randomUUID().toString();
                    System.out.println("Token generado: " + token);

                    // Retornar respuesta con datos del usuario encontrado
                    return LoginResponse.builder()
                            .id(usuario.getId())
                            .username(usuario.getUsername())
                            .nombre(usuario.getNombre())
                            .rol(usuario.getRol())
                            .token(token)
                            .build();
                }
            } catch (Exception ex) {
                System.err.println("Error al actualizar último acceso: " + ex.getMessage());
            }

            // Si no se encontró el usuario pero las credenciales son admin/admin
            String token = UUID.randomUUID().toString();
            System.out.println("Token generado para admin (fallback): " + token);

            return LoginResponse.builder()
                    .id(1L)
                    .username("admin")
                    .nombre("Administrador")
                    .rol(Rol.ADMIN)
                    .token(token)
                    .build();
        }

        // Código normal de autenticación
        try {
            // Buscar usuario
            Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

            // Verificar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                throw new RuntimeException("Usuario o contraseña incorrectos");
            }

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Crear token simple (en una implementación real usaríamos JWT)
            String token = UUID.randomUUID().toString();
            System.out.println("Token generado para " + usuario.getUsername() + ": " + token);

            // Retornar respuesta
            return LoginResponse.builder()
                    .id(usuario.getId())
                    .username(usuario.getUsername())
                    .nombre(usuario.getNombre())
                    .rol(usuario.getRol())
                    .token(token)
                    .build();
        } catch (Exception e) {
            System.err.println("Error en autenticación normal: " + e.getMessage());
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
    }
}
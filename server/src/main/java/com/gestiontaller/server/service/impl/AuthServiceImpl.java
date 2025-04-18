package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.auth.LoginRequest;
import com.gestiontaller.server.dto.auth.LoginResponse;
import com.gestiontaller.server.model.usuario.Usuario;
import com.gestiontaller.server.repository.usuario.UsuarioRepository;
import com.gestiontaller.server.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public LoginResponse login(LoginRequest loginRequest) {
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

        // Retornar respuesta
        return LoginResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .token(token)
                .build();
    }
}
package com.gestiontaller.server.dto.auth;

import com.gestiontaller.server.model.usuario.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String nombre;
    private String token;
    private Rol rol;
}
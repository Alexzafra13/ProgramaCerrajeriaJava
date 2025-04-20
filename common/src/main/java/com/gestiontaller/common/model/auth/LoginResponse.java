package com.gestiontaller.common.model.auth;

import com.gestiontaller.common.model.usuario.Rol;
import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String nombre;
    private String token;
    private Rol rol;
}
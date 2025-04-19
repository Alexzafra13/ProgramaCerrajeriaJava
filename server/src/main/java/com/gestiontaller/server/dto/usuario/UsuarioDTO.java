package com.gestiontaller.server.dto.usuario;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private String preferenciasVisuales;
}
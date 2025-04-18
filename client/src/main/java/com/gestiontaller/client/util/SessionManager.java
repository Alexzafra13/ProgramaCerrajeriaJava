package com.gestiontaller.client.util;

import com.gestiontaller.client.model.auth.LoginResponse;
import com.gestiontaller.client.model.usuario.Rol;

public class SessionManager {

    private static SessionManager instance;

    private Long userId;
    private String username;
    private String nombreUsuario;
    private String token;
    private Rol rol;

    private SessionManager() {
        // Constructor privado para singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoginInfo(LoginResponse loginResponse) {
        this.userId = loginResponse.getId();
        this.username = loginResponse.getUsername();
        this.nombreUsuario = loginResponse.getNombre();
        this.token = loginResponse.getToken();
        this.rol = loginResponse.getRol();
    }

    public void clearSession() {
        this.userId = null;
        this.username = null;
        this.nombreUsuario = null;
        this.token = null;
        this.rol = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getToken() {
        return token;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public boolean hasRole(Rol... roles) {
        if (rol == null) return false;

        for (Rol r : roles) {
            if (rol == r) return true;
        }
        return false;
    }
}
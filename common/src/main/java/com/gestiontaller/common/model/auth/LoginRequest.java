package com.gestiontaller.common.model.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
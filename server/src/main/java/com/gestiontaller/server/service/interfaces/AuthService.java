package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.auth.LoginRequest;
import com.gestiontaller.server.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
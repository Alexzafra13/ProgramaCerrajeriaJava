package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.auth.LoginRequest;
import com.gestiontaller.common.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
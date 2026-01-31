package com.project.artists.service;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;

/**
 * Interface para serviços de autenticação
 */
public interface AuthService {
    
    AuthResponseDTO login(LoginRequestDTO loginRequest);
    
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest);
}

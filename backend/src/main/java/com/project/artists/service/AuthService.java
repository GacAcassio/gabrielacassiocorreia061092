package com.project.artists.service;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;

/**
 * Interface para serviços de autenticação
 */
public interface AuthService {
    
    /**
     * Realiza login e retorna tokens JWT
     */
    AuthResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * Renova access token usando refresh token
     */
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest);
}

package com.project.artists.service.impl;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;
import com.project.artists.exception.UnauthorizedException;
import com.project.artists.security.jwt.JwtTokenProvider;
import com.project.artists.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; 
import org.springframework.stereotype.Service;

/**
 * Implementação do serviço de autenticação
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Autenticar usuário com Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Gerar tokens JWT
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsername());

            // Retornar resposta
            return new AuthResponseDTO(
                accessToken,
                refreshToken,
                tokenProvider.getExpirationInSeconds()
            );

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Credenciais inválidas");
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Erro na autenticação: " + e.getMessage());
        }
    }

    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        // Validar refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido ou expirado");
        }

        // Extrair username do refresh token
        String username = tokenProvider.getUsernameFromToken(refreshToken);

        // Gerar novo access token
        String newAccessToken = tokenProvider.generateAccessToken(username);

        // Retornar resposta (mesmo refresh token)
        return new AuthResponseDTO(
            newAccessToken,
            refreshToken,
            tokenProvider.getExpirationInSeconds()
        );
    }
}

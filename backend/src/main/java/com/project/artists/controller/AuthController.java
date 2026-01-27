package com.project.artists.controller;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;
import com.project.artists.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para endpoints de autenticação
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e renovação de tokens")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Realiza login e retorna tokens JWT
     * 
     * @param loginRequest Credenciais do usuário
     * @return Tokens de acesso e refresh
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica usuário e retorna tokens JWT (access token expira em 5 minutos)")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Renova access token usando refresh token
     * 
     * @param refreshRequest Refresh token
     * @return Novo access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar Token", description = "Gera novo access token usando refresh token válido")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshRequest) {
        AuthResponseDTO response = authService.refreshToken(refreshRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de teste (público)
     */
    @GetMapping("/test")
    @Operation(summary = "Testar API", description = "Endpoint público para testar se API está respondendo")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API está funcionando! Use POST /api/v1/auth/login para autenticar.");
    }
}

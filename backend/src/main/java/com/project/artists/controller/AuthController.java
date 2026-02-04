package com.project.artists.controller;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;
import com.project.artists.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para endpoints de autenticacao
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Autenticacao", description = "Endpoints para login, logout e renovacao de tokens JWT")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Realiza login e retorna tokens JWT
     */
    @PostMapping("/login")
    @SecurityRequirements // Endpoint publico
    @Operation(
        summary = "Fazer Login",
        description = "Autentica o usuario com username e password, retornando tokens JWT.\n\n" +
                     "**Access Token:**\n" +
                     "- Expira em 5 minutos\n" +
                     "- Usado para acessar endpoints protegidos\n\n" +
                     "**Refresh Token:**\n" +
                     "- Expira em 7 dias\n" +
                     "- Usado para renovar access token expirado\n\n" +
                     "**Credenciais Padrao:**\n" +
                     "- Username: `admin`\n" +
                     "- Password: `admin123`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class),
                examples = @ExampleObject(
                    name = "Sucesso",
                    value = "{\n" +
                           "  \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                           "  \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                           "  \"tokenType\": \"Bearer\",\n" +
                           "  \"expiresIn\": 300\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais invalidas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro",
                    value = "{\n" +
                           "  \"status\": 401,\n" +
                           "  \"message\": \"Credenciais invalidas\",\n" +
                           "  \"timestamp\": \"2026-02-03T10:30:00\"\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada invalidos",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<AuthResponseDTO> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciais de login",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequestDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de Login",
                    value = "{\n" +
                           "  \"username\": \"admin\",\n" +
                           "  \"password\": \"admin123\"\n" +
                           "}"
                )
            )
        )
        @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Renova access token usando refresh token
     */
    @PostMapping("/refresh")
    @SecurityRequirements // Endpoint publico
    @Operation(
        summary = "Renovar Access Token",
        description = "Gera um novo access token usando um refresh token valido.\n\n" +
                     "**Quando usar:**\n" +
                     "- Quando access token expirar (apos 5 minutos)\n" +
                     "- Para evitar fazer login novamente\n\n" +
                     "**Nota:** O refresh token tem validade de 7 dias."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token renovado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class),
                examples = @ExampleObject(
                    name = "Sucesso",
                    value = "{\n" +
                           "  \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                           "  \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                           "  \"tokenType\": \"Bearer\",\n" +
                           "  \"expiresIn\": 300\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token invalido ou expirado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<AuthResponseDTO> refreshToken(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refresh token",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RefreshTokenRequestDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de Refresh",
                    value = "{\n" +
                           "  \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"\n" +
                           "}"
                )
            )
        )
        @Valid @RequestBody RefreshTokenRequestDTO refreshRequest
    ) {
        AuthResponseDTO response = authService.refreshToken(refreshRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de teste (publico)
     */
    @GetMapping("/test")
    @SecurityRequirements // Endpoint publico
    @Operation(
        summary = "Testar API",
        description = "Endpoint publico para verificar se a API esta funcionando corretamente.\n\n" +
                     "**Uso:**\n" +
                     "- Nao requer autenticacao\n" +
                     "- Retorna mensagem de sucesso se API estiver online"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "API funcionando normalmente",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "API esta funcionando! Use POST /api/v1/auth/login para autenticar."
                )
            )
        )
    })
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API esta funcionando! Use POST /api/v1/auth/login para autenticar.");
    }
}
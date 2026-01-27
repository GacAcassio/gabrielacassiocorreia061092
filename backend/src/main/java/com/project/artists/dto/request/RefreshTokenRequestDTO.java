package com.project.artists.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para renovação de token
 */
public class RefreshTokenRequestDTO {
    
    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
    
    // Construtores
    public RefreshTokenRequestDTO() {}
    
    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters e Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

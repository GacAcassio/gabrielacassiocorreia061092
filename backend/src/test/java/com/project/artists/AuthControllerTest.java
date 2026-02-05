package com.project.artists.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.response.AuthResponseDTO;
import com.project.artists.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AuthController")
class AuthControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController authController;
    
    private LoginRequestDTO loginRequest;
    private AuthResponseDTO authResponse;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(authController)
            .build();
        
        objectMapper = new ObjectMapper();
        
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");
        
        authResponse = new AuthResponseDTO();
        authResponse.setAccessToken("access.token.jwt");
        authResponse.setRefreshToken("refresh.token.jwt");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(300L);
    }
    
    @Test
    @DisplayName("POST /auth/login - Deve fazer login com sucesso")
    void deveFazerLoginComSucesso() throws Exception {
        when(authService.login(any(LoginRequestDTO.class)))
            .thenReturn(authResponse);
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access.token.jwt"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(300));
        
        verify(authService).login(any(LoginRequestDTO.class));
    }
    
    @Test
    @DisplayName("GET /auth/test - Deve retornar mensagem de sucesso")
    void deveRetornarMensagemDeSucesso() throws Exception {
        mockMvc.perform(get("/api/v1/auth/test"))
                .andExpect(status().isOk());
    }
}
package com.project.artists.service;

import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import com.project.artists.entity.User;
import com.project.artists.repository.UserRepository;
import com.project.artists.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Autenticacao - AuthService")
class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setEmail("admin@test.com");
    }
    
    @Test
    @DisplayName("Deve validar DTOs")
    void deveValidarDTOs() {
        LoginRequestDTO login = new LoginRequestDTO("admin", "admin123");
        RefreshTokenRequestDTO refresh = new RefreshTokenRequestDTO("token");
        
        assertEquals("admin", login.getUsername());
        assertEquals("token", refresh.getRefreshToken());
    }
}

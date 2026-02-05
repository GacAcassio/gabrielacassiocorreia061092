package com.project.artists.dto;

import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.request.LoginRequestDTO;
import com.project.artists.dto.request.RefreshTokenRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Validacao de DTOs")
class DTOValidationTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("LoginRequestDTO - Deve validar com sucesso")
    void loginDTODeveValidarComSucesso() {
        LoginRequestDTO dto = new LoginRequestDTO("admin", "admin123");
        
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("LoginRequestDTO - Deve falhar com username vazio")
    void loginDTODeveFalharComUsernameVazio() {
        LoginRequestDTO dto = new LoginRequestDTO("", "admin123");
        
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }
    
    @Test
    @DisplayName("LoginRequestDTO - Deve falhar com password vazio")
    void loginDTODeveFalharComPasswordVazio() {
        LoginRequestDTO dto = new LoginRequestDTO("admin", "");
        
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
    
    @Test
    @DisplayName("RefreshTokenRequestDTO - Deve validar com sucesso")
    void refreshTokenDTODeveValidarComSucesso() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("valid.refresh.token");
        
        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("RefreshTokenRequestDTO - Deve falhar com token vazio")
    void refreshTokenDTODeveFalharComTokenVazio() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("");
        
        Set<ConstraintViolation<RefreshTokenRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve validar com sucesso")
    void artistDTODeveValidarComSucesso() {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setName("Queen");
        dto.setBio("British rock band");
        
        Set<ConstraintViolation<ArtistRequestDTO>> violations = validator.validate(dto);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve falhar com nome vazio")
    void artistDTODeveFalharComNomeVazio() {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setName("");
        
        Set<ConstraintViolation<ArtistRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve falhar com nome muito curto")
    void artistDTODeveFalharComNomeMuitoCurto() {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setName("A"); // Menos de 2 caracteres
        
        Set<ConstraintViolation<ArtistRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve falhar com nome muito longo")
    void artistDTODeveFalharComNomeMuitoLongo() {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setName("A".repeat(201)); // Mais de 200 caracteres
        
        Set<ConstraintViolation<ArtistRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve validar com sucesso")
    void albumDTODeveValidarComSucesso() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setTitle("A Night at the Opera");
        dto.setArtistIds(Arrays.asList(1L));
        dto.setReleaseYear(1975);
        
        Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve falhar com titulo vazio")
    void albumDTODeveFalharComTituloVazio() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setTitle("");
        dto.setArtistIds(Arrays.asList(1L));
        
        Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve falhar sem artistas")
    void albumDTODeveFalharSemArtistas() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setTitle("Album");
        dto.setArtistIds(Arrays.asList()); // Lista vazia
        
        Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("artistIds")));
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve aceitar multiplos artistas")
    void albumDTODeveAceitarMultiplosArtistas() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setTitle("Collaboration Album");
        dto.setArtistIds(Arrays.asList(1L, 2L, 3L));
        
        Set<ConstraintViolation<AlbumRequestDTO>> violations = validator.validate(dto);
        
        assertTrue(violations.isEmpty());
    }
}

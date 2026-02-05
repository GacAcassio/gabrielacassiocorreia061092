package com.project.artists.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Request DTOs")
class RequestDTOTest {
    
    @Test
    @DisplayName("LoginRequestDTO - Deve criar com construtor vazio")
    void loginRequestDeveCriarComConstrutorVazio() {
        LoginRequestDTO dto = new LoginRequestDTO();
        
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }
    
    @Test
    @DisplayName("LoginRequestDTO - Deve criar com construtor completo")
    void loginRequestDeveCriarComConstrutorCompleto() {
        LoginRequestDTO dto = new LoginRequestDTO("admin", "admin123");
        
        assertEquals("admin", dto.getUsername());
        assertEquals("admin123", dto.getPassword());
    }
    
    @Test
    @DisplayName("LoginRequestDTO - Deve permitir setar valores")
    void loginRequestDevePermitirSetarValores() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("user");
        dto.setPassword("pass");
        
        assertEquals("user", dto.getUsername());
        assertEquals("pass", dto.getPassword());
    }

    @Test
    @DisplayName("RefreshTokenRequestDTO - Deve criar com construtor vazio")
    void refreshTokenRequestDeveCriarComConstrutorVazio() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        
        assertNotNull(dto);
        assertNull(dto.getRefreshToken());
    }
    
    @Test
    @DisplayName("RefreshTokenRequestDTO - Deve criar com construtor completo")
    void refreshTokenRequestDeveCriarComConstrutorCompleto() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO("refresh.token.jwt");
        
        assertEquals("refresh.token.jwt", dto.getRefreshToken());
    }
    
    @Test
    @DisplayName("RefreshTokenRequestDTO - Deve permitir setar token")
    void refreshTokenRequestDevePermitirSetarToken() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setRefreshToken("new.token");
        
        assertEquals("new.token", dto.getRefreshToken());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve criar com construtor vazio")
    void artistRequestDeveCriarComConstrutorVazio() {
        ArtistRequestDTO dto = new ArtistRequestDTO();
        
        assertNotNull(dto);
        assertNull(dto.getName());
        assertNull(dto.getBio());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve criar com construtor completo")
    void artistRequestDeveCriarComConstrutorCompleto() {
        ArtistRequestDTO dto = new ArtistRequestDTO("Queen", "British rock band");
        
        assertEquals("Queen", dto.getName());
        assertEquals("British rock band", dto.getBio());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve aceitar bio null")
    void artistRequestDeveAceitarBioNull() {
        ArtistRequestDTO dto = new ArtistRequestDTO("Artist", null);
        
        assertEquals("Artist", dto.getName());
        assertNull(dto.getBio());
    }
    
    @Test
    @DisplayName("ArtistRequestDTO - Deve aceitar bio longa")
    void artistRequestDeveAceitarBioLonga() {
        String longBio = "A".repeat(5000);
        ArtistRequestDTO dto = new ArtistRequestDTO();
        dto.setBio(longBio);
        
        assertEquals(5000, dto.getBio().length());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve criar com construtor vazio")
    void albumRequestDeveCriarComConstrutorVazio() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        
        assertNotNull(dto);
        assertNull(dto.getTitle());
        assertNotNull(dto.getArtistIds());
        assertTrue(dto.getArtistIds().isEmpty());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve criar com construtor completo")
    void albumRequestDeveCriarComConstrutorCompleto() {
        AlbumRequestDTO dto = new AlbumRequestDTO(
            "A Night at the Opera",
            Arrays.asList(1L),
            1975
        );
        
        assertEquals("A Night at the Opera", dto.getTitle());
        assertEquals(1, dto.getArtistIds().size());
        assertEquals(1L, dto.getArtistIds().get(0));
        assertEquals(1975, dto.getReleaseYear());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve aceitar multiplos artistas")
    void albumRequestDeveAceitarMultiplosArtistas() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setArtistIds(Arrays.asList(1L, 2L, 3L));
        
        assertEquals(3, dto.getArtistIds().size());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve aceitar releaseYear null")
    void albumRequestDeveAceitarReleaseYearNull() {
        AlbumRequestDTO dto = new AlbumRequestDTO("Album", Arrays.asList(1L), null);
        
        assertNull(dto.getReleaseYear());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve permitir atualizar campos")
    void albumRequestDevePermitirAtualizarCampos() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        
        dto.setTitle("New Album");
        dto.setArtistIds(Arrays.asList(5L, 6L));
        dto.setReleaseYear(2020);
        
        assertEquals("New Album", dto.getTitle());
        assertEquals(2, dto.getArtistIds().size());
        assertEquals(2020, dto.getReleaseYear());
    }
    
    @Test
    @DisplayName("AlbumRequestDTO - Deve manter lista vazia de artistas")
    void albumRequestDeveManterListaVaziaDeArtistas() {
        AlbumRequestDTO dto = new AlbumRequestDTO();
        dto.setArtistIds(Collections.emptyList());
        
        assertNotNull(dto.getArtistIds());
        assertTrue(dto.getArtistIds().isEmpty());
    }
}

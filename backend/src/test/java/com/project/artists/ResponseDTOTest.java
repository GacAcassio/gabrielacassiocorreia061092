package com.project.artists.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Response DTOs")
class ResponseDTOTest {

    @Test
    @DisplayName("AuthResponseDTO - Deve criar com construtor vazio")
    void authResponseDeveCrearComConstrutorVazio() {
        AuthResponseDTO dto = new AuthResponseDTO();
        
        assertNotNull(dto);
        assertEquals("Bearer", dto.getTokenType());
    }
    
    @Test
    @DisplayName("AuthResponseDTO - Deve criar com construtor completo")
    void authResponseDeveCrearComConstrutorCompleto() {
        AuthResponseDTO dto = new AuthResponseDTO("access.token", "refresh.token", 300L);
        
        assertEquals("access.token", dto.getAccessToken());
        assertEquals("refresh.token", dto.getRefreshToken());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(300L, dto.getExpiresIn());
    }
    
    @Test
    @DisplayName("AuthResponseDTO - Deve permitir alterar tokenType")
    void authResponseDevePermitirAlterarTokenType() {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setTokenType("Custom");
        
        assertEquals("Custom", dto.getTokenType());
    }

    @Test
    @DisplayName("ArtistSummaryDTO - Deve criar com construtor completo")
    void artistSummaryDeveCrearComConstrutorCompleto() {
        ArtistSummaryDTO dto = new ArtistSummaryDTO(1L, "Queen", 15);
        
        assertEquals(1L, dto.getId());
        assertEquals("Queen", dto.getName());
        assertEquals(15, dto.getAlbumCount());
    }
    
    @Test
    @DisplayName("ArtistSummaryDTO - Deve aceitar albumCount null")
    void artistSummaryDeveAceitarAlbumCountNull() {
        ArtistSummaryDTO dto = new ArtistSummaryDTO(1L, "Artist", null);
        
        assertNull(dto.getAlbumCount());
    }
    
    @Test
    @DisplayName("ArtistResponseDTO - Deve criar com construtor completo")
    void artistResponseDeveCrearComConstrutorCompleto() {
        ArtistResponseDTO dto = new ArtistResponseDTO(1L, "Queen", "British rock band", 15);
        
        assertEquals(1L, dto.getId());
        assertEquals("Queen", dto.getName());
        assertEquals("British rock band", dto.getBio());
        assertEquals(15, dto.getAlbumCount());
        assertNotNull(dto.getAlbums());
        assertTrue(dto.getAlbums().isEmpty());
    }
    
    @Test
    @DisplayName("ArtistResponseDTO - Deve adicionar albums")
    void artistResponseDeveAdicionarAlbums() {
        ArtistResponseDTO dto = new ArtistResponseDTO();
        ArtistResponseDTO.AlbumSummaryDTO album = new ArtistResponseDTO.AlbumSummaryDTO(1L, "Album", 1975);
        
        dto.setAlbums(Arrays.asList(album));
        
        assertEquals(1, dto.getAlbums().size());
        assertEquals("Album", dto.getAlbums().get(0).getTitle());
    }
    
    @Test
    @DisplayName("ArtistResponseDTO - Deve definir timestamps")
    void artistResponseDeveDefinirTimestamps() {
        ArtistResponseDTO dto = new ArtistResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
    
    
    @Test
    @DisplayName("AlbumSummaryDTO - Deve criar com construtor completo")
    void albumSummaryDeveCrearComConstrutorCompleto() {
        AlbumSummaryDTO dto = new AlbumSummaryDTO(1L, "A Night at the Opera", 1975);
        
        assertEquals(1L, dto.getId());
        assertEquals("A Night at the Opera", dto.getTitle());
        assertEquals(1975, dto.getReleaseYear());
        assertNotNull(dto.getArtistNames());
        assertTrue(dto.getArtistNames().isEmpty());
    }
    
    @Test
    @DisplayName("AlbumSummaryDTO - Deve adicionar nomes de artistas")
    void albumSummaryDeveAdicionarNomesDeArtistas() {
        AlbumSummaryDTO dto = new AlbumSummaryDTO();
        dto.setArtistNames(Arrays.asList("Queen", "David Bowie"));
        
        assertEquals(2, dto.getArtistNames().size());
        assertTrue(dto.getArtistNames().contains("Queen"));
    }
    
    @Test
    @DisplayName("AlbumSummaryDTO - Deve definir coverUrl")
    void albumSummaryDeveDefinirCoverUrl() {
        AlbumSummaryDTO dto = new AlbumSummaryDTO();
        dto.setCoverUrl("http://example.com/cover.jpg");
        
        assertEquals("http://example.com/cover.jpg", dto.getCoverUrl());
    }
    
    @Test
    @DisplayName("AlbumResponseDTO - Deve criar com construtor completo")
    void albumResponseDeveCrearComConstrutorCompleto() {
        AlbumResponseDTO dto = new AlbumResponseDTO(1L, "Album", 1980);
        
        assertEquals(1L, dto.getId());
        assertEquals("Album", dto.getTitle());
        assertEquals(1980, dto.getReleaseYear());
        assertNotNull(dto.getArtists());
        assertNotNull(dto.getCoverUrls());
    }
    
    @Test
    @DisplayName("AlbumResponseDTO - Deve adicionar artistas")
    void albumResponseDeveAdicionarArtistas() {
        AlbumResponseDTO dto = new AlbumResponseDTO();
        AlbumResponseDTO.ArtistSummaryDTO artist = new AlbumResponseDTO.ArtistSummaryDTO(1L, "Queen");
        
        dto.setArtists(Arrays.asList(artist));
        
        assertEquals(1, dto.getArtists().size());
        assertEquals("Queen", dto.getArtists().get(0).getName());
    }
    
    @Test
    @DisplayName("AlbumResponseDTO - Deve adicionar coverUrls")
    void albumResponseDeveAdicionarCoverUrls() {
        AlbumResponseDTO dto = new AlbumResponseDTO();
        List<String> urls = Arrays.asList(
            "http://example.com/cover1.jpg",
            "http://example.com/cover2.jpg"
        );
        
        dto.setCoverUrls(urls);
        
        assertEquals(2, dto.getCoverUrls().size());
    }
    
    @Test
    @DisplayName("PageResponseDTO - Deve criar com construtor vazio")
    void pageResponseDeveCrearComConstrutorVazio() {
        PageResponseDTO<String> dto = new PageResponseDTO<>();
        
        assertNotNull(dto);
    }
    
    @Test
    @DisplayName("PageResponseDTO - Deve criar com construtor completo")
    void pageResponseDeveCrearComConstrutorCompleto() {
        List<String> content = Arrays.asList("item1", "item2", "item3");
        PageResponseDTO<String> dto = new PageResponseDTO<>(content, 0, 10, 3, 1);
        
        assertEquals(3, dto.getContent().size());
        assertEquals(0, dto.getPageNumber());
        assertEquals(10, dto.getPageSize());
        assertEquals(3, dto.getTotalElements());
        assertEquals(1, dto.getTotalPages());
        assertTrue(dto.isFirst());
        assertTrue(dto.isLast());
        assertFalse(dto.isEmpty());
    }
    
    @Test
    @DisplayName("PageResponseDTO - Deve identificar pagina vazia")
    void pageResponseDeveIdentificarPaginaVazia() {
        List<String> content = Arrays.asList();
        PageResponseDTO<String> dto = new PageResponseDTO<>(content, 0, 10, 0, 0);
        
        assertTrue(dto.isEmpty());
        assertTrue(dto.getContent().isEmpty());
    }
    
    @Test
    @DisplayName("PageResponseDTO - Deve identificar primeira e ultima pagina")
    void pageResponseDeveIdentificarPrimeiraEUltimaPagina() {
        List<String> content = Arrays.asList("item");
        
        // Primeira pagina
        PageResponseDTO<String> firstPage = new PageResponseDTO<>(content, 0, 10, 25, 3);
        assertTrue(firstPage.isFirst());
        assertFalse(firstPage.isLast());
        
        // Ultima pagina
        PageResponseDTO<String> lastPage = new PageResponseDTO<>(content, 2, 10, 25, 3);
        assertFalse(lastPage.isFirst());
        assertTrue(lastPage.isLast());
    }
    
    @Test
    @DisplayName("SyncResponseDTO - Deve criar com timestamp automatico")
    void syncResponseDeveCriarComTimestampAutomatico() {
        SyncResponseDTO dto = new SyncResponseDTO();
        
        assertNotNull(dto);
        assertNotNull(dto.getTimestamp());
    }
    
    @Test
    @DisplayName("SyncResponseDTO - Deve criar com construtor completo")
    void syncResponseDeveCriarComConstrutorCompleto() {
        SyncResponseDTO.SyncStats stats = new SyncResponseDTO.SyncStats(
            50, 45, 5, 2, 3, 40, 234
        );
        
        SyncResponseDTO dto = new SyncResponseDTO(true, "Sucesso", stats);
        
        assertTrue(dto.isSuccess());
        assertEquals("Sucesso", dto.getMessage());
        assertNotNull(dto.getStats());
        assertEquals(50, dto.getStats().getTotalExterno());
    }
    
    @Test
    @DisplayName("SyncResponseDTO.SyncStats - Deve criar com todos os campos")
    void syncStatsDeveCriarComTodosCampos() {
        SyncResponseDTO.SyncStats stats = new SyncResponseDTO.SyncStats(
            100, 95, 10, 5, 8, 82, 500
        );
        
        assertEquals(100, stats.getTotalExterno());
        assertEquals(95, stats.getTotalLocal());
        assertEquals(10, stats.getNovos());
        assertEquals(5, stats.getInativos());
        assertEquals(8, stats.getAlterados());
        assertEquals(82, stats.getSemMudancas());
        assertEquals(500, stats.getTempoExecucaoMs());
    }
    
    @Test
    @DisplayName("SyncResponseDTO - Deve adicionar erros")
    void syncResponseDeveAdicionarErros() {
        SyncResponseDTO dto = new SyncResponseDTO();
        dto.setErrors(Arrays.asList("Erro 1", "Erro 2"));
        
        assertNotNull(dto.getErrors());
        assertEquals(2, dto.getErrors().size());
        assertTrue(dto.getErrors().contains("Erro 1"));
    }
}

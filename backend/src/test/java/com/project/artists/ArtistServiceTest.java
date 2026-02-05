package com.project.artists.service;

import com.project.artists.dto.notification.NotificationType;
import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.dto.response.ArtistSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.entity.Artist;
import com.project.artists.exception.ResourceNotFoundException;
import com.project.artists.repository.ArtistRepository;
import com.project.artists.service.impl.ArtistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Artistas - ArtistService")
class ArtistServiceTest {
    
    @Mock
    private ArtistRepository artistRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private ArtistServiceImpl artistService;
    
    private Artist testArtist;
    private ArtistRequestDTO artistRequest;
    
    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Queen");
        testArtist.setBio("British rock band");
        
        artistRequest = new ArtistRequestDTO();
        artistRequest.setName("Queen");
        artistRequest.setBio("British rock band");
    }
    
    @Test
    @DisplayName("Deve criar artista com sucesso")
    void deveCriarArtistaComSucesso() {
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);
        doNothing().when(notificationService).sendNotification(
            any(NotificationType.class), 
            anyString(), 
            anyString(), 
            any()
        );
        
        ArtistResponseDTO response = artistService.create(artistRequest);
        
        assertNotNull(response);
        assertEquals("Queen", response.getName());
        
        verify(artistRepository).save(any(Artist.class));
        verify(notificationService).sendNotification(
            eq(NotificationType.ARTIST_CREATED), 
            anyString(), 
            anyString(), 
            any()
        );
    }
    
    @Test
    @DisplayName("Deve buscar artista por ID")
    void deveBuscarArtistaPorId() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        
        ArtistResponseDTO response = artistService.findById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Queen", response.getName());
        
        verify(artistRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Deve lancar excecao quando artista nao encontrado")
    void deveLancarExcecaoQuandoArtistaNaoEncontrado() {
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            artistService.findById(999L);
        });
        
        verify(artistRepository).findById(999L);
    }
    
    @Test
    @DisplayName("Deve listar artistas com paginacao")
    void deveListarArtistasComPaginacao() {
        List<Artist> artists = Arrays.asList(testArtist);
        Page<Artist> page = new PageImpl<>(artists);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(artistRepository.findAll(pageable)).thenReturn(page);
        
        PageResponseDTO<ArtistSummaryDTO> response = artistService.findAll(pageable);
        
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        
        verify(artistRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Deve deletar artista")
    void deveDeletarArtista() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        doNothing().when(artistRepository).delete(testArtist);
        doNothing().when(notificationService).sendNotification(
            any(NotificationType.class), 
            anyString(), 
            anyString()
        );
        
        artistService.delete(1L);
        
        verify(artistRepository).findById(1L);
        verify(artistRepository).delete(testArtist);
        verify(notificationService).sendNotification(
            eq(NotificationType.ARTIST_DELETED), 
            anyString(), 
            anyString()
        );
    }
}
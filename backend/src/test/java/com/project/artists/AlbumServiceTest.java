package com.project.artists.service;

import com.project.artists.dto.notification.NotificationType;
import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.entity.Album;
import com.project.artists.entity.Artist;
import com.project.artists.exception.ResourceNotFoundException;
import com.project.artists.repository.AlbumRepository;
import com.project.artists.repository.ArtistRepository;
import com.project.artists.service.impl.AlbumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Albuns - AlbumService")
class AlbumServiceTest {
    
    @Mock
    private AlbumRepository albumRepository;
    
    @Mock
    private ArtistRepository artistRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private AlbumServiceImpl albumService;
    
    private Album testAlbum;
    private Artist testArtist;
    private AlbumRequestDTO albumRequest;
    
    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Queen");
        
        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setTitle("A Night at the Opera");
        testAlbum.setReleaseYear(1975);
        testAlbum.setArtists(new HashSet<>(Arrays.asList(testArtist)));
        
        albumRequest = new AlbumRequestDTO();
        albumRequest.setTitle("A Night at the Opera");
        albumRequest.setReleaseYear(1975);
        albumRequest.setArtistIds(Arrays.asList(1L));
    }
    
    @Test
    @DisplayName("Deve criar album com sucesso")
    void deveCriarAlbumComSucesso() {
        // Configurar mock para retornar artista quando buscar por ID individual
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);
        doNothing().when(notificationService).sendNotification(
            any(NotificationType.class), 
            anyString(), 
            anyString(), 
            any()
        );
        
        AlbumResponseDTO response = albumService.create(albumRequest);
        
        assertNotNull(response);
        assertEquals("A Night at the Opera", response.getTitle());
        
        verify(artistRepository, atLeastOnce()).findById(1L);
        verify(albumRepository).save(any(Album.class));
        verify(notificationService).sendNotification(
            eq(NotificationType.ALBUM_CREATED), 
            anyString(), 
            anyString(), 
            any()
        );
    }
    
    @Test
    @DisplayName("Deve buscar album por ID")
    void deveBuscarAlbumPorId() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        
        AlbumResponseDTO response = albumService.findById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        
        verify(albumRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Deve lancar excecao quando album nao encontrado")
    void deveLancarExcecaoQuandoAlbumNaoEncontrado() {
        when(albumRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            albumService.findById(999L);
        });
    }
    
    @Test
    @DisplayName("Deve deletar album")
    void deveDeletarAlbum() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        doNothing().when(albumRepository).delete(testAlbum);
        doNothing().when(notificationService).sendNotification(
            any(NotificationType.class), 
            anyString(), 
            anyString()
        );
        
        albumService.delete(1L);
        
        verify(albumRepository).findById(1L);
        verify(albumRepository).delete(testAlbum);
        verify(notificationService).sendNotification(
            eq(NotificationType.ALBUM_DELETED), 
            anyString(), 
            anyString()
        );
    }
}
package com.project.artists.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.artists.dto.request.AlbumRequestDTO;
import com.project.artists.dto.response.AlbumResponseDTO;
import com.project.artists.dto.response.AlbumSummaryDTO;
import com.project.artists.dto.response.PageResponseDTO;
import com.project.artists.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de AlbumController")
class AlbumControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private AlbumService albumService;
    
    @InjectMocks
    private AlbumController albumController;
    
    private AlbumRequestDTO albumRequest;
    private AlbumResponseDTO albumResponse;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(albumController)
            .build();
        
        objectMapper = new ObjectMapper();
        
        albumRequest = new AlbumRequestDTO();
        albumRequest.setTitle("A Night at the Opera");
        albumRequest.setReleaseYear(1975);
        albumRequest.setArtistIds(Arrays.asList(1L));
        
        albumResponse = new AlbumResponseDTO();
        albumResponse.setId(1L);
        albumResponse.setTitle("A Night at the Opera");
        albumResponse.setReleaseYear(1975);
    }
    
    @Test
    @DisplayName("POST /albums - Deve criar album")
    void deveCrearAlbum() throws Exception {
        when(albumService.create(any(AlbumRequestDTO.class))).thenReturn(albumResponse);
        
        mockMvc.perform(post("/api/v1/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("A Night at the Opera"));
        
        verify(albumService).create(any(AlbumRequestDTO.class));
    }
    
    @Test
    @DisplayName("GET /albums/{id} - Deve buscar album por ID")
    void deveBuscarAlbumPorId() throws Exception {
        when(albumService.findById(1L)).thenReturn(albumResponse);
        
        mockMvc.perform(get("/api/v1/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("A Night at the Opera"));
        
        verify(albumService).findById(1L);
    }
    
    @Test
    @DisplayName("GET /albums - Deve listar albums")
    void deveListarAlbums() throws Exception {
        PageResponseDTO<AlbumSummaryDTO> page = new PageResponseDTO<>();
        page.setContent(Collections.emptyList());
        page.setTotalElements(0L);
        
        when(albumService.findAll(any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/albums")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        
        verify(albumService).findAll(any(Pageable.class));
    }
    
    @Test
    @DisplayName("PUT /albums/{id} - Deve atualizar album")
    void deveAtualizarAlbum() throws Exception {
        when(albumService.update(eq(1L), any(AlbumRequestDTO.class))).thenReturn(albumResponse);
        
        mockMvc.perform(put("/api/v1/albums/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        
        verify(albumService).update(eq(1L), any(AlbumRequestDTO.class));
    }
    
    @Test
    @DisplayName("DELETE /albums/{id} - Deve deletar album")
    void deveDeletarAlbum() throws Exception {
        doNothing().when(albumService).delete(1L);
        
        mockMvc.perform(delete("/api/v1/albums/1"))
                .andExpect(status().isNoContent());
        
        verify(albumService).delete(1L);
    }
    
    @Test
    @DisplayName("POST /albums/{id}/covers - Deve fazer upload de capas")
    void deveFazerUploadDeCapas() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "files",
            "cover.jpg",
            "image/jpeg",
            "test image".getBytes()
        );
        
        when(albumService.uploadCovers(eq(1L), anyList())).thenReturn(albumResponse);
        
        mockMvc.perform(multipart("/api/v1/albums/1/covers")
                .file(file))
                .andExpect(status().isOk());
        
        verify(albumService).uploadCovers(eq(1L), anyList());
    }
    
    @Test
    @DisplayName("GET /albums/search - Deve buscar por titulo")
    void deveBuscarPorTitulo() throws Exception {
        PageResponseDTO<AlbumSummaryDTO> page = new PageResponseDTO<>();
        when(albumService.searchByTitle(eq("Night"), any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/albums/search")
                .param("title", "Night"))
                .andExpect(status().isOk());
        
        verify(albumService).searchByTitle(eq("Night"), any(Pageable.class));
    }
}
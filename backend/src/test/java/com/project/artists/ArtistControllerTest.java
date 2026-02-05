package com.project.artists.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.artists.dto.request.ArtistRequestDTO;
import com.project.artists.dto.response.ArtistResponseDTO;
import com.project.artists.service.ArtistService;
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
@DisplayName("Testes de ArtistController")
class ArtistControllerTest {
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @Mock
    private ArtistService artistService;
    
    @InjectMocks
    private ArtistController artistController;
    
    private ArtistRequestDTO artistRequest;
    private ArtistResponseDTO artistResponse;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(artistController)
            .build();
        
        objectMapper = new ObjectMapper();
        
        artistRequest = new ArtistRequestDTO();
        artistRequest.setName("Queen");
        artistRequest.setBio("British rock band");
        
        artistResponse = new ArtistResponseDTO();
        artistResponse.setId(1L);
        artistResponse.setName("Queen");
        artistResponse.setBio("British rock band");
    }
    
    @Test
    @DisplayName("POST /artists - Deve criar artista")
    void deveCriarArtista() throws Exception {
        when(artistService.create(any(ArtistRequestDTO.class)))
            .thenReturn(artistResponse);
        
        mockMvc.perform(post("/api/v1/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(artistRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Queen"));
        
        verify(artistService).create(any(ArtistRequestDTO.class));
    }
    
    @Test
    @DisplayName("GET /artists/{id} - Deve buscar artista por ID")
    void deveBuscarArtistaPorId() throws Exception {
        when(artistService.findById(1L)).thenReturn(artistResponse);
        
        mockMvc.perform(get("/api/v1/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Queen"));
        
        verify(artistService).findById(1L);
    }
    
    @Test
    @DisplayName("DELETE /artists/{id} - Deve deletar artista")
    void deveDeletarArtista() throws Exception {
        doNothing().when(artistService).delete(1L);
        
        mockMvc.perform(delete("/api/v1/artists/1"))
                .andExpect(status().isNoContent());
        
        verify(artistService).delete(1L);
    }
}
package com.project.artists.controller;

import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;
import com.project.artists.service.RegionalSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de RegionalController")
class RegionalControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private RegionalSyncService regionalSyncService;
    
    @InjectMocks
    private RegionalController regionalController;
    
    private SyncResponseDTO syncResponse;
    private List<Regional> regionais;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(regionalController)
            .build();
        
        syncResponse = new SyncResponseDTO();
        syncResponse.setSuccess(true);
        syncResponse.setMessage("Sincronizacao concluida");
        
        Regional regional1 = new Regional();
        regional1.setId(1);
        regional1.setNome("Regional Sul");
        regional1.setAtivo(true);
        
        Regional regional2 = new Regional();
        regional2.setId(2);
        regional2.setNome("Regional Norte");
        regional2.setAtivo(true);
        
        regionais = Arrays.asList(regional1, regional2);
    }
    
    @Test
    @DisplayName("POST /regionais/sync - Deve sincronizar com sucesso")
    void deveSincronizarComSucesso() throws Exception {
        when(regionalSyncService.sincronizar()).thenReturn(syncResponse);
        
        mockMvc.perform(post("/api/v1/regionais/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sincronizacao concluida"));
        
        verify(regionalSyncService).sincronizar();
    }
    
    @Test
    @DisplayName("POST /regionais/sync - Deve retornar 500 em caso de erro")
    void deveRetornar500EmCasoDeErro() throws Exception {
        syncResponse.setSuccess(false);
        syncResponse.setMessage("Erro na sincronizacao");
        
        when(regionalSyncService.sincronizar()).thenReturn(syncResponse);
        
        mockMvc.perform(post("/api/v1/regionais/sync"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
        
        verify(regionalSyncService).sincronizar();
    }
    
    @Test
    @DisplayName("GET /regionais - Deve listar regionais ativos")
    void deveListarRegionaisAtivos() throws Exception {
        when(regionalSyncService.listarAtivos()).thenReturn(regionais);
        
        mockMvc.perform(get("/api/v1/regionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Regional Sul"))
                .andExpect(jsonPath("$[1].id").value(2));
        
        verify(regionalSyncService).listarAtivos();
    }
    
    @Test
    @DisplayName("GET /regionais/all - Deve listar todos os regionais")
    void deveListarTodosRegionais() throws Exception {
        when(regionalSyncService.listarTodos()).thenReturn(regionais);
        
        mockMvc.perform(get("/api/v1/regionais/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        
        verify(regionalSyncService).listarTodos();
    }
}
package com.project.artists.service;

import com.project.artists.client.RegionalApiClient;
import com.project.artists.dto.external.RegionalExternoDTO;
import com.project.artists.dto.response.SyncResponseDTO;
import com.project.artists.entity.Regional;
import com.project.artists.repository.RegionalRepository;
import com.project.artists.service.impl.RegionalSyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para RegionalSyncService
 * 
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Sincronizacao de Regionais")
class RegionalSyncServiceTest {
    
    @Mock
    private RegionalApiClient regionalApiClient;
    
    @Mock
    private RegionalRepository regionalRepository;
    
    @InjectMocks
    private RegionalSyncServiceImpl regionalSyncService;
    
    private List<RegionalExternoDTO> regionaisExternos;
    private List<Regional> regionaisLocais;
    
    @BeforeEach
    void setUp() {
        regionaisExternos = new ArrayList<>();
        regionaisLocais = new ArrayList<>();
    }
    
    @Test
    @DisplayName("Deve inserir todos os regionais na primeira sincronizacao")
    void testPrimeiraSincronizacao() {
        // Arrange - Preparar dados
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul", true));
        regionaisExternos.add(new RegionalExternoDTO(2, "Regional Norte", true));
        regionaisExternos.add(new RegionalExternoDTO(3, "Regional Centro", true));
        
        // Banco local vazio
        regionaisLocais.clear();
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act - Executar
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert - Verificar
        assertTrue(result.isSuccess());
        assertNotNull(result.getStats());
        
        assertEquals(3, result.getStats().getTotalExterno());
        assertEquals(0, result.getStats().getTotalLocal());
        assertEquals(3, result.getStats().getNovos());
        assertEquals(0, result.getStats().getInativos());
        assertEquals(0, result.getStats().getAlterados());
        assertEquals(0, result.getStats().getSemMudancas());
        
        // Verificar que saveAll foi chamado 1 vez (para inserir novos)
        verify(regionalRepository, times(1)).saveAll(any());
    }
    
    @Test
    @DisplayName("Deve reportar sem mudancas quando dados sao identicos")
    void testSincronizacaoSemMudancas() {
        // Arrange
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul", true));
        regionaisExternos.add(new RegionalExternoDTO(2, "Regional Norte", true));
        
        regionaisLocais.add(new Regional(1, "Regional Sul", true));
        regionaisLocais.add(new Regional(2, "Regional Norte", true));
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(2, result.getStats().getTotalExterno());
        assertEquals(2, result.getStats().getTotalLocal());
        assertEquals(0, result.getStats().getNovos());
        assertEquals(0, result.getStats().getInativos());
        assertEquals(0, result.getStats().getAlterados());
        assertEquals(2, result.getStats().getSemMudancas());
        
        // Nao deve chamar saveAll (nenhuma mudanca)
        verify(regionalRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Deve inserir apenas novos regionais")
    void testNovosRegionais() {
        // Arrange
        // Externo tem 3 regionais
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul", true));
        regionaisExternos.add(new RegionalExternoDTO(2, "Regional Norte", true));
        regionaisExternos.add(new RegionalExternoDTO(3, "Regional Centro", true));
        
        // Local tem apenas 2
        regionaisLocais.add(new Regional(1, "Regional Sul", true));
        regionaisLocais.add(new Regional(2, "Regional Norte", true));
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getStats().getNovos()); // Regional Centro e novo
        assertEquals(2, result.getStats().getSemMudancas());
        
        verify(regionalRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Deve marcar como inativo regionais removidos da API")
    void testRegionaisRemovidos() {
        // Arrange
        // Externo tem apenas 1
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul", true));
        
        // Local tem 3 (2 serao marcados como removidos)
        regionaisLocais.add(new Regional(1, "Regional Sul", true));
        regionaisLocais.add(new Regional(2, "Regional Norte", true));
        regionaisLocais.add(new Regional(3, "Regional Centro", true));
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(0, result.getStats().getNovos());
        assertEquals(2, result.getStats().getInativos()); // Norte e Centro foram removidos
        assertEquals(1, result.getStats().getSemMudancas()); // Sul continua
        
        verify(regionalRepository, times(1)).saveAll(any());
    }
    
    @Test
    @DisplayName("Deve atualizar regionais com nome alterado")
    void testRegionaisAlterados() {
        // Arrange
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul Novo", true)); // Nome mudou
        regionaisExternos.add(new RegionalExternoDTO(2, "Regional Norte", true));
        
        regionaisLocais.add(new Regional(1, "Regional Sul", true)); // Nome antigo
        regionaisLocais.add(new Regional(2, "Regional Norte", true));
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getStats().getAlterados()); // Regional Sul foi alterado
        assertEquals(1, result.getStats().getSemMudancas()); // Regional Norte nao mudou
        
        // O saveAll pode ser chamado 1 ou 2 vezes dependendo da implementação
        // (uma vez para alterados, outra para novos/inativos ou tudo junto)
        verify(regionalRepository, atLeastOnce()).saveAll(any());
    }
    
    @Test
    @DisplayName("Deve processar corretamente novos, removidos e alterados simultaneamente")
    void testCenarioMisto() {
        // Arrange
        // API Externa: IDs 1, 2(nome mudou), 4(novo)
        regionaisExternos.add(new RegionalExternoDTO(1, "Regional Sul", true));      // Sem mudanca
        regionaisExternos.add(new RegionalExternoDTO(2, "Regiao Norte", true));      // Alterado
        regionaisExternos.add(new RegionalExternoDTO(4, "Regional Leste", true));    // Novo
        
        // Banco Local: IDs 1, 2(nome antigo), 3(sera removido)
        regionaisLocais.add(new Regional(1, "Regional Sul", true));                  // Sem mudanca
        regionaisLocais.add(new Regional(2, "Regional Norte", true));                // Sera alterado
        regionaisLocais.add(new Regional(3, "Regional Centro", true));               // Sera removido
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getStats().getNovos());        // ID 4
        assertEquals(1, result.getStats().getInativos());     // ID 3
        assertEquals(1, result.getStats().getAlterados());    // ID 2
        assertEquals(1, result.getStats().getSemMudancas());  // ID 1
    }
    
    @Test
    @DisplayName("Deve tratar erro quando API externa falha")
    void testErroApiExterna() {
        // Arrange
        when(regionalApiClient.buscarRegionais())
            .thenThrow(new RuntimeException("Falha ao comunicar com API externa"));
        
        // Act
        SyncResponseDTO result = regionalSyncService.sincronizar();
        
        // Assert
        assertFalse(result.isSuccess(), "O resultado deve indicar falha");
        assertNotNull(result.getMessage(), "Deve ter mensagem de erro");
        // Aceitar qualquer mensagem de erro, pois pode variar dependendo da implementação
        assertFalse(result.getMessage().isEmpty(), "Mensagem de erro não pode estar vazia");
        
        // Nao deve tentar salvar nada
        verify(regionalRepository, never()).saveAll(any());
    }
     
    @Test
    @DisplayName("Deve processar 1000 regionais em menos de 5 segundos")
    void testPerformance() {
        // Arrange - Simular 1000 regionais externos e 950 locais
        for (int i = 1; i <= 1000; i++) {
            regionaisExternos.add(new RegionalExternoDTO(i, "Regional " + i, true));
        }
        
        for (int i = 1; i <= 950; i++) {
            regionaisLocais.add(new Regional(i, "Regional " + i, true));
        }
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        when(regionalRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        long startTime = System.currentTimeMillis();
        SyncResponseDTO result = regionalSyncService.sincronizar();
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1000, result.getStats().getTotalExterno());
        assertEquals(950, result.getStats().getTotalLocal());
        assertEquals(50, result.getStats().getNovos()); 
        
        // Verificar performance - deve ser muito rapido (< 5s)
        assertTrue(duration < 5000, 
            "Sincronizacao de 1000 regionais levou " + duration + "ms (esperado < 5000ms)");
        
        System.out.println("Performance: " + duration + "ms para 1000 regionais");
    }

    @Test
    @DisplayName("Deve listar apenas regionais ativos")
    void testListarAtivos() {
        // Arrange
        regionaisLocais.add(new Regional(1, "Regional Sul", true));
        regionaisLocais.add(new Regional(2, "Regional Norte", true));
        
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        
        // Act
        List<Regional> result = regionalSyncService.listarAtivos();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getAtivo()));
    }
    
    @Test
    @DisplayName("Deve listar todos os regionais (ativos e inativos)")
    void testListarTodos() {
        // Arrange
        List<Regional> todos = Arrays.asList(
            new Regional(1, "Regional Sul", true),
            new Regional(2, "Regional Norte", false),
            new Regional(3, "Regional Centro", true)
        );
        
        when(regionalRepository.findAll()).thenReturn(todos);
        
        // Act
        List<Regional> result = regionalSyncService.listarTodos();
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }
    
    @Test
    @DisplayName("Deve demonstrar complexidade O(n+m) vs O(n*m)")
    void testComplexidadeAlgoritmica() {
        // Este teste demonstra que nossa abordagem e O(n+m) e nao O(n*m)
        
        // Arrange - Cenario pequeno
        for (int i = 1; i <= 100; i++) {
            regionaisExternos.add(new RegionalExternoDTO(i, "Regional " + i, true));
            regionaisLocais.add(new Regional(i, "Regional " + i, true));
        }
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        
        // Act - Medir tempo pequeno
        long start1 = System.currentTimeMillis();
        regionalSyncService.sincronizar();
        long time1 = System.currentTimeMillis() - start1;
        
        // Arrange - Cenario 10x maior
        regionaisExternos.clear();
        regionaisLocais.clear();
        for (int i = 1; i <= 1000; i++) {
            regionaisExternos.add(new RegionalExternoDTO(i, "Regional " + i, true));
            regionaisLocais.add(new Regional(i, "Regional " + i, true));
        }
        
        when(regionalApiClient.buscarRegionais()).thenReturn(regionaisExternos);
        when(regionalRepository.findByAtivoTrue()).thenReturn(regionaisLocais);
        
        // Act - Medir tempo grande
        long start2 = System.currentTimeMillis();
        regionalSyncService.sincronizar();
        long time2 = System.currentTimeMillis() - start2;
        
        // Assert - Se fosse O(n*m), tempo seria 100x maior
        // Como e O(n+m), tempo deve ser ~10x maior (linear)
        double ratio = (double) time2 / Math.max(time1, 1); // Evita divisão por zero
        
        System.out.println("100 regionais: " + time1 + "ms");
        System.out.println("1000 regionais: " + time2 + "ms");
        System.out.println("Ratio: " + ratio + "x");
        
        // Se fosse O(n*m): ratio seria ~100x
        // Como e O(n+m): ratio deve ser ~10x
        assertTrue(ratio < 50, 
            "Algoritmo deveria ser O(n+m) mas ratio e " + ratio + "x (esperado < 50x)");
    }
}
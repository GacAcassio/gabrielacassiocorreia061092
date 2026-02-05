package com.project.artists.dto.external;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de RegionalExternoDTO")
class RegionalExternoDTOTest {
    
    @Test
    @DisplayName("Deve criar DTO vazio")
    void deveCriarDTOVazio() {
        RegionalExternoDTO dto = new RegionalExternoDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getAtivo());
    }
    
    @Test
    @DisplayName("Deve criar DTO com construtor completo")
    void deveCriarDTOComConstrutorCompleto() {
        RegionalExternoDTO dto = new RegionalExternoDTO(1, "Regional Sul", true);
        
        assertEquals(1, dto.getId());
        assertEquals("Regional Sul", dto.getNome());
        assertTrue(dto.getAtivo());
    }
    
    @Test
    @DisplayName("Deve definir e obter valores via setters/getters")
    void deveDefinirEObterValores() {
        RegionalExternoDTO dto = new RegionalExternoDTO();
        
        dto.setId(10);
        dto.setNome("Regional Norte");
        dto.setAtivo(false);
        
        assertEquals(10, dto.getId());
        assertEquals("Regional Norte", dto.getNome());
        assertFalse(dto.getAtivo());
    }
    
    @Test
    @DisplayName("Deve gerar toString corretamente")
    void deveGerarToString() {
        RegionalExternoDTO dto = new RegionalExternoDTO(5, "Regional Centro", true);
        
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("id=5"));
        assertTrue(toString.contains("nome='Regional Centro'"));
        assertTrue(toString.contains("ativo=true"));
    }
    
    @Test
    @DisplayName("Deve aceitar valores null")
    void deveAceitarValoresNull() {
        RegionalExternoDTO dto = new RegionalExternoDTO(null, null, null);
        
        assertNull(dto.getId());
        assertNull(dto.getNome());
        assertNull(dto.getAtivo());
    }
}

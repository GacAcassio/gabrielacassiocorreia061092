package com.project.artists.dto.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de NotificationType")
class NotificationTypeTest {
    
    @Test
    @DisplayName("Deve ter todos os tipos definidos")
    void deveTerTodosOsTiposDefinidos() {
        NotificationType[] types = NotificationType.values();
        
        assertTrue(types.length >= 8);
        
        // Verificar se todos os tipos principais existem
        assertNotNull(NotificationType.valueOf("ALBUM_CREATED"));
        assertNotNull(NotificationType.valueOf("ALBUM_UPDATED"));
        assertNotNull(NotificationType.valueOf("ALBUM_DELETED"));
        assertNotNull(NotificationType.valueOf("ALBUM_COVER_UPLOADED"));
        assertNotNull(NotificationType.valueOf("ARTIST_CREATED"));
        assertNotNull(NotificationType.valueOf("ARTIST_UPDATED"));
        assertNotNull(NotificationType.valueOf("ARTIST_DELETED"));
        assertNotNull(NotificationType.valueOf("SYSTEM_NOTIFICATION"));
    }
    
    @Test
    @DisplayName("Deve ter displayName para cada tipo")
    void deveTerDisplayNameParaCadaTipo() {
        for (NotificationType type : NotificationType.values()) {
            assertNotNull(type.getDisplayName());
            assertFalse(type.getDisplayName().isEmpty());
        }
    }
    
    @Test
    @DisplayName("Deve ter displayNames corretos")
    void deveTerDisplayNamesCorretos() {
        assertEquals("Novo Álbum Cadastrado", NotificationType.ALBUM_CREATED.getDisplayName());
        assertEquals("Álbum Atualizado", NotificationType.ALBUM_UPDATED.getDisplayName());
        assertEquals("Álbum Removido", NotificationType.ALBUM_DELETED.getDisplayName());
        assertEquals("Nova Capa de Álbum", NotificationType.ALBUM_COVER_UPLOADED.getDisplayName());
        assertEquals("Novo Artista Cadastrado", NotificationType.ARTIST_CREATED.getDisplayName());
        assertEquals("Artista Atualizado", NotificationType.ARTIST_UPDATED.getDisplayName());
        assertEquals("Artista Removido", NotificationType.ARTIST_DELETED.getDisplayName());
        assertEquals("Notificação do Sistema", NotificationType.SYSTEM_NOTIFICATION.getDisplayName());
    }
    
    @Test
    @DisplayName("Deve converter string para enum")
    void deveConverterStringParaEnum() {
        NotificationType type = NotificationType.valueOf("ARTIST_CREATED");
        
        assertEquals(NotificationType.ARTIST_CREATED, type);
        assertEquals("Novo Artista Cadastrado", type.getDisplayName());
    }
    
    @Test
    @DisplayName("Deve lancar excecao para tipo invalido")
    void deveLancarExcecaoParaTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            NotificationType.valueOf("INVALID_TYPE");
        });
    }
}

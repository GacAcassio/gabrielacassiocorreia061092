package com.project.artists.dto.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de NotificationDTO")
class NotificationDTOTest {
    
    @Test
    @DisplayName("Deve criar notificacao com construtor vazio")
    void deveCriarComConstrutorVazio() {
        NotificationDTO notification = new NotificationDTO();
        
        assertNotNull(notification);
        assertNotNull(notification.getTimestamp());
    }
    
    @Test
    @DisplayName("Deve criar notificacao com tipo, titulo e mensagem")
    void deveCriarComTipoTituloMensagem() {
        NotificationDTO notification = new NotificationDTO(
            NotificationType.ARTIST_CREATED,
            "Novo Artista",
            "Queen foi adicionado"
        );
        
        assertNotNull(notification);
        assertEquals(NotificationType.ARTIST_CREATED, notification.getType());
        assertEquals("Novo Artista", notification.getTitle());
        assertEquals("Queen foi adicionado", notification.getMessage());
        assertNotNull(notification.getTimestamp());
        assertNull(notification.getData());
    }
    
    @Test
    @DisplayName("Deve criar notificacao com dados adicionais")
    void deveCriarComDadosAdicionais() {
        Map<String, Object> data = new HashMap<>();
        data.put("artistId", 1L);
        data.put("artistName", "Queen");
        
        NotificationDTO notification = new NotificationDTO(
            NotificationType.ARTIST_CREATED,
            "Novo Artista",
            "Queen foi adicionado",
            data
        );
        
        assertNotNull(notification);
        assertEquals(NotificationType.ARTIST_CREATED, notification.getType());
        assertEquals("Novo Artista", notification.getTitle());
        assertEquals("Queen foi adicionado", notification.getMessage());
        assertEquals(data, notification.getData());
        assertNotNull(notification.getTimestamp());
    }
    
    @Test
    @DisplayName("Deve permitir setar todos os campos")
    void devePermitirSetarTodosCampos() {
        NotificationDTO notification = new NotificationDTO();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        
        notification.setType(NotificationType.ALBUM_CREATED);
        notification.setTitle("Teste");
        notification.setMessage("Mensagem de teste");
        notification.setData(data);
        notification.setTimestamp(now);
        
        assertEquals(NotificationType.ALBUM_CREATED, notification.getType());
        assertEquals("Teste", notification.getTitle());
        assertEquals("Mensagem de teste", notification.getMessage());
        assertEquals(data, notification.getData());
        assertEquals(now, notification.getTimestamp());
    }
    
    @Test
    @DisplayName("Deve criar notificacao para cada tipo")
    void deveCriarParaCadaTipo() {
        NotificationType[] types = NotificationType.values();
        
        for (NotificationType type : types) {
            NotificationDTO notification = new NotificationDTO(
                type,
                "Titulo",
                "Mensagem"
            );
            
            assertEquals(type, notification.getType());
            assertNotNull(type.getDisplayName());
        }
    }
}

package com.project.artists.dto.notification;

/**
 * Tipos de notificações disponíveis no sistema
 */
public enum NotificationType {
    
    ALBUM_CREATED("Novo Álbum Cadastrado"),
    
    ALBUM_UPDATED("Álbum Atualizado"),
    
    ALBUM_DELETED("Álbum Removido"),
    
    ALBUM_COVER_UPLOADED("Nova Capa de Álbum"),
    
    ARTIST_CREATED("Novo Artista Cadastrado"),
    
    ARTIST_UPDATED("Artista Atualizado"),

    ARTIST_DELETED("Artista Removido"),
    
    SYSTEM_NOTIFICATION("Notificação do Sistema");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

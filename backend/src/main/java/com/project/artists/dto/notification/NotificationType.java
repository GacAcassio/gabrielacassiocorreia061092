package com.project.artists.dto.notification;

/**
 * Tipos de notificações disponíveis no sistema
 * 
 * Cada tipo representa um evento específico que pode gerar
 * uma notificação em tempo real para os clientes conectados
 */
public enum NotificationType {
    
    /**
     * Novo álbum foi cadastrado no sistema
     */
    ALBUM_CREATED("Novo Álbum Cadastrado"),
    
    /**
     * Álbum existente foi atualizado
     */
    ALBUM_UPDATED("Álbum Atualizado"),
    
    /**
     * Álbum foi removido do sistema
     */
    ALBUM_DELETED("Álbum Removido"),
    
    /**
     * Nova capa foi adicionada a um álbum
     */
    ALBUM_COVER_UPLOADED("Nova Capa de Álbum"),
    
    /**
     * Novo artista foi cadastrado
     */
    ARTIST_CREATED("Novo Artista Cadastrado"),
    
    /**
     * Artista existente foi atualizado
     */
    ARTIST_UPDATED("Artista Atualizado"),
    
    /**
     * Artista foi removido do sistema
     */
    ARTIST_DELETED("Artista Removido"),
    
    /**
     * Notificação genérica do sistema
     */
    SYSTEM_NOTIFICATION("Notificação do Sistema");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

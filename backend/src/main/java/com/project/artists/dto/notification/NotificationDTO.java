package com.project.artists.dto.notification;

import java.time.LocalDateTime;

/**
 * DTO para notificações em tempo real via WebSocket
 * 
 * Representa uma mensagem de notificação que será enviada
 * aos clientes conectados via WebSocket quando eventos importantes ocorrem
 */
public class NotificationDTO {
    
    /**
     * Tipo da notificação
     */
    private NotificationType type;
    
    /**
     * Título da notificação
     */
    private String title;
    
    /**
     * Mensagem detalhada
     */
    private String message;
    
    /**
     * Dados adicionais (opcional)
     * Pode conter informações específicas do evento
     */
    private Object data;
    
    /**
     * Timestamp da notificação
     */
    private LocalDateTime timestamp;
    
    // Construtores
    public NotificationDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    public NotificationDTO(NotificationType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public NotificationDTO(NotificationType type, String title, String message, Object data) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters e Setters
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

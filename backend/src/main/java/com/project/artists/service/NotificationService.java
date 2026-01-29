package com.project.artists.service;

import com.project.artists.dto.notification.NotificationDTO;
import com.project.artists.dto.notification.NotificationType;

/**
 * Interface do serviço de notificações em tempo real
 * 
 * Responsável por enviar notificações via WebSocket para todos
 * os clientes conectados ao sistema
 */
public interface NotificationService {
    
    /**
     * Envia uma notificação para todos os clientes conectados
     * 
     * @param notification DTO com os dados da notificação
     */
    void sendNotification(NotificationDTO notification);
    
    /**
     * Envia uma notificação simples com tipo, título e mensagem
     * 
     * @param type Tipo da notificação
     * @param title Título da notificação
     * @param message Mensagem da notificação
     */
    void sendNotification(NotificationType type, String title, String message);
    
    /**
     * Envia uma notificação com dados adicionais
     * 
     * @param type Tipo da notificação
     * @param title Título da notificação
     * @param message Mensagem da notificação
     * @param data Dados adicionais (objeto que será serializado em JSON)
     */
    void sendNotification(NotificationType type, String title, String message, Object data);
}

package com.project.artists.service.impl;

import com.project.artists.dto.notification.NotificationDTO;
import com.project.artists.dto.notification.NotificationType;
import com.project.artists.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementação do serviço de notificações em tempo real via WebSocket
 * 
 * Utiliza SimpMessagingTemplate do Spring para enviar mensagens
 * STOMP para o broker configurado
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    /**
     * Tópico de destino para notificações
     * Clientes devem se inscrever em "/topic/notifications" para receber mensagens
     */
    private static final String NOTIFICATION_TOPIC = "/topic/notifications";
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Envia notificação completa para todos os clientes conectados
     */
    @Override
    public void sendNotification(NotificationDTO notification) {
        try {
            logger.info("Enviando notificação: {} - {}", 
                       notification.getType(), 
                       notification.getTitle());
            
            // Envia mensagem para o tópico /topic/notifications
            messagingTemplate.convertAndSend(NOTIFICATION_TOPIC, notification);
            
            logger.debug("Notificação enviada com sucesso para {}", NOTIFICATION_TOPIC);
            
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação via WebSocket: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Envia notificação simples (sem dados adicionais)
     */
    @Override
    public void sendNotification(NotificationType type, String title, String message) {
        NotificationDTO notification = new NotificationDTO(type, title, message);
        sendNotification(notification);
    }
    
    /**
     * Envia notificação com dados adicionais
     */
    @Override
    public void sendNotification(NotificationType type, String title, String message, Object data) {
        NotificationDTO notification = new NotificationDTO(type, title, message, data);
        sendNotification(notification);
    }
}

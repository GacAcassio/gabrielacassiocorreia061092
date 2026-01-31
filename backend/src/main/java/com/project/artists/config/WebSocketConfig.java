package com.project.artists.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração do WebSocket com STOMP (Simple Text Oriented Messaging Protocol)
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura o message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita broker simples em memória para tópicos de notificação
        config.enableSimpleBroker("/topic");
        
        // Define prefixo para mensagens destinadas aos controllers
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra endpoint WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  
                .withSockJS();  
    }
}

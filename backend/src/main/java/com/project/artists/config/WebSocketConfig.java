package com.project.artists.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração WebSocket
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000,http://artists-frontend:3000}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //logger.info("Configurando Message Broker");
        
        // Broker simples em memória para broadcast
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefixo para mensagens enviadas de clientes para o servidor
        config.setApplicationDestinationPrefixes("/app");
        
        //logger.info("Message Broker configurado - Broker: /topic, /queue | App prefix: /app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //logger.info("Registrando STOMP Endpoints");
        
        String[] origins = allowedOrigins.split(",");
        //logger.info("Allowed origins: {}", String.join(", ", origins));
        
        // Registra endpoint /ws que aceita SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(origins)
                .withSockJS();
        
        //logger.info("STOMP Endpoint registrado: /ws (com SockJS)");
    }
}
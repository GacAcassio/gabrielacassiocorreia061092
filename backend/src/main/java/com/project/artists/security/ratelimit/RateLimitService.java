package com.project.artists.security.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Serviço para controle de rate limiting
 * Implementa algoritmo de Sliding Window
 * Limite: 10 requisições por minuto por usuário
 */
@Service
public class RateLimitService {
    
    @Value("${app.rate-limit.requests:10}")
    private int maxRequests;
    
    @Value("${app.rate-limit.duration:60}")
    private int durationSeconds;
    
    // Mapa: username -> fila de timestamps de requisições
    private final Map<String, Queue<Long>> requestsMap = new ConcurrentHashMap<>();
    
    /**
     * Verifica se usuário pode fazer requisição
     * 
     * @param username Identificador do usuário
     * @return true se permitido, false se excedeu limite
     */
    public boolean allowRequest(String username) {
        long now = System.currentTimeMillis();
        long windowStart = now - (durationSeconds * 1000L);
        
        // Obter ou criar fila de timestamps para o usuário
        Queue<Long> timestamps = requestsMap.computeIfAbsent(
            username, 
            k -> new ConcurrentLinkedQueue<>()
        );
        
        // Remover timestamps fora da janela de tempo
        timestamps.removeIf(timestamp -> timestamp < windowStart);
        
        // Verificar se excedeu o limite
        if (timestamps.size() >= maxRequests) {
            return false; // Rate limit excedido
        }
        
        // Adicionar timestamp atual
        timestamps.add(now);
        
        return true;
    }
    
    /**
     * Retorna quantas requisições ainda podem ser feitas
     */
    public int getRemainingRequests(String username) {
        long now = System.currentTimeMillis();
        long windowStart = now - (durationSeconds * 1000L);
        
        Queue<Long> timestamps = requestsMap.get(username);
        if (timestamps == null) {
            return maxRequests;
        }
        
        // Remover timestamps expirados
        timestamps.removeIf(timestamp -> timestamp < windowStart);
        
        return Math.max(0, maxRequests - timestamps.size());
    }
    
    /**
     * Retorna tempo em segundos até poder fazer próxima requisição
     */
    public long getRetryAfter(String username) {
        Queue<Long> timestamps = requestsMap.get(username);
        if (timestamps == null || timestamps.isEmpty()) {
            return 0;
        }
        
        // Pegar o timestamp mais antigo
        Long oldestTimestamp = timestamps.peek();
        if (oldestTimestamp == null) {
            return 0;
        }
        
        long now = System.currentTimeMillis();
        long windowStart = now - (durationSeconds * 1000L);
        
        // Calcular quanto tempo até o timestamp mais antigo sair da janela
        long retryAfterMs = (oldestTimestamp + (durationSeconds * 1000L)) - now;
        
        return Math.max(0, retryAfterMs / 1000);
    }
    
    /**
     * Limpa dados de um usuário (útil para testes)
     */
    public void reset(String username) {
        requestsMap.remove(username);
    }
    
    /**
     * Limpa todos os dados (útil para testes)
     */
    public void resetAll() {
        requestsMap.clear();
    }
}

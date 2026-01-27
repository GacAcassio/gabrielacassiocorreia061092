package com.project.artists.security.ratelimit;

import com.project.artists.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que aplica rate limiting nas requisições autenticadas
 * Limite: 10 requisições por minuto por usuário
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Obter autenticação do contexto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Aplicar rate limit apenas para usuários autenticados
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            String username = authentication.getName();
            
            // Verificar se pode fazer requisição
            if (!rateLimitService.allowRequest(username)) {
                long retryAfter = rateLimitService.getRetryAfter(username);
                
                // Adicionar headers de rate limit
                response.setHeader("X-RateLimit-Limit", "10");
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("X-RateLimit-Reset", String.valueOf(retryAfter));
                response.setHeader("Retry-After", String.valueOf(retryAfter));
                
                // Lançar exceção que será tratada pelo GlobalExceptionHandler
                throw new RateLimitExceededException(
                    "Limite de requisições excedido. Tente novamente em " + retryAfter + " segundos.",
                    retryAfter
                );
            }
            
            // Adicionar headers informativos
            int remaining = rateLimitService.getRemainingRequests(username);
            response.setHeader("X-RateLimit-Limit", "10");
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        }
        
        // Continuar chain de filtros
        filterChain.doFilter(request, response);
    }
    
    /**
     * Não aplicar rate limit em endpoints públicos de autenticação
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Não aplicar em endpoints de auth (login, refresh)
        return path.startsWith("/api/v1/auth/") 
            || path.startsWith("/actuator/")
            || path.startsWith("/swagger-ui/")
            || path.startsWith("/v3/api-docs/");
    }
}

package com.project.artists.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro de Rate Limiting - Limita requisições por usuário
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired
    private RateLimitService rateLimitService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Sempre ignora OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        // Lista COMPLETA de paths WebSocket/SockJS que devem ser ignorados
        boolean isWebSocketPath =
                path.startsWith("/ws") ||
                path.startsWith("/ws/") ||
                path.startsWith("/app/") ||
                path.startsWith("/topic/") ||
                path.startsWith("/queue/") ||
                path.contains("/websocket") ||
                path.contains("/sockjs") ||
                path.contains("/xhr") ||
                path.contains("/eventsource") ||
                path.contains("/htmlfile") ||
                path.contains("/jsonp") ||
                path.endsWith("/info");

        // Outros endpoints públicos
        boolean isPublicPath =
                path.startsWith("/api/v1/auth/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");

        boolean shouldSkip = isWebSocketPath || isPublicPath;

        if (isWebSocketPath) {
            //logger.debug("RateLimitFilter: Ignorando path WebSocket: {}", path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Aplica rate limit apenas para usuários autenticados (não anonymous)
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();

            // Se NÃO pode fazer requisição -> retorna 429 e encerra
            if (!rateLimitService.allowRequest(username)) {
                long retryAfter = rateLimitService.getRetryAfter(username);

                response.setStatus(429); // Too Many Requests
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                // Headers úteis para o front
                response.setHeader("X-RateLimit-Limit", "10");
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("X-RateLimit-Reset", String.valueOf(retryAfter));
                response.setHeader("Retry-After", String.valueOf(retryAfter));

                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("status", 429);
                body.put("error", "Too Many Requests");
                body.put("message", "Limite de requisições excedido. Tente novamente em " + retryAfter + " segundos.");
                body.put("retryAfter", retryAfter);
                body.put("path", request.getRequestURI());

                // logger.warn("Rate limit excedido para usuário={} path={} retryAfter={}s",
                //         username, request.getRequestURI(), retryAfter);

                objectMapper.writeValue(response.getWriter(), body);
                return; 
            }

            // Se passou no rate limit -> adiciona headers informativos
            int remaining = rateLimitService.getRemainingRequests(username);
            response.setHeader("X-RateLimit-Limit", "10");
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        }

        // Continua chain normalmente
        filterChain.doFilter(request, response);
    }
}

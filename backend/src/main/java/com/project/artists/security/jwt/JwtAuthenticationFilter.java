package com.project.artists.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT - Valida tokens APENAS para API REST
 * WebSocket paths são COMPLETAMENTE IGNORADOS
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
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
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs/") ||
            path.startsWith("/actuator/");
        
        boolean shouldSkip = isWebSocketPath || isPublicPath;
        
       // if (isWebSocketPath) {
        //    logger.debug("JwtAuthFilter: Ignorando path WebSocket: {}", path);
       // }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                //logger.debug("Usuário autenticado: {}", username);
            }
        } catch (Exception ex) {
            //logger.error(" Erro ao autenticar usuário: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
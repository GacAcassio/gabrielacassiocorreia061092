package com.project.artists.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Ponto de entrada para tratar erros de autenticação
 * Retorna 401 Unauthorized quando token é inválido ou não fornecido
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonResponse = String.format(
            "{\"status\":%d,\"message\":\"%s\",\"error\":\"Unauthorized\"}",
            HttpServletResponse.SC_UNAUTHORIZED,
            "Token JWT ausente ou invalido"
        );
        
        response.getWriter().write(jsonResponse);
    }
}

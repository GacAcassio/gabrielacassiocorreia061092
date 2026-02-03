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
import java.util.Arrays;
import java.util.List;

/**
 * Filtro que intercepta requisições e valida o token JWT
 * 
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    // Endpoints públicos que não precisam de autenticação
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/v1/auth/",
        "/actuator/",
        "/swagger-ui/",
        "/v3/api-docs/",
        "/ws/"
    );
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Log da requisição
        //logger.debug("=== JWT Filter === Method: {} | Path: {}", method, path);
        
        // Pular endpoints públicos
        if (isPublicEndpoint(path)) {
            //logger.debug("Endpoint público, pulando autenticação JWT: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Extrair token do header Authorization
            String jwt = getJwtFromRequest(request);
            
            if (!StringUtils.hasText(jwt)) {
                //logger.debug("Nenhum token JWT encontrado no header Authorization");
                filterChain.doFilter(request, response);
                return;
            }
            
            //logger.debug("Token JWT encontrado, iniciando validação...");
            
            // Validar token
            if (!tokenProvider.validateToken(jwt)) {
                //logger.warn("Token JWT inválido ou expirado");
                // NÃO retornar aqui, deixar continuar para que o SecurityConfig trate
                filterChain.doFilter(request, response);
                return;
            }
            
            //logger.debug("Token JWT válido!");
            
            // 3. Extrair username do token
            String username = null;
            try {
                username = tokenProvider.getUsernameFromToken(jwt);
                //logger.debug("Username extraído do token: {}", username);
            } catch (Exception ex) {
                //logger.error("Erro ao extrair username do token: {}", ex.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
            
            if (username == null || username.isEmpty()) {
                //logger.error("Username vazio no token!");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Verificar se já está autenticado
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                //logger.debug("Usuário já autenticado no contexto: {}", 
                           //SecurityContextHolder.getContext().getAuthentication().getName());
                filterChain.doFilter(request, response);
                return;
            }
            
            // Carregar usuário do banco
            UserDetails userDetails = null;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
                //logger.debug("UserDetails carregado: username={}, authorities={}", 
                          // userDetails.getUsername(), 
                          // userDetails.getAuthorities());
            } catch (Exception ex) {
                //logger.error("Erro ao carregar UserDetails para username '{}': {}", 
                           //username, ex.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
            
            if (userDetails == null) {
                //logger.error("UserDetails é null para username: {}", username);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Criar autenticação
            UsernamePasswordAuthenticationToken authentication = null;
            try {
                authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                //logger.debug("UsernamePasswordAuthenticationToken criado com sucesso");
            } catch (Exception ex) {
                //logger.error("Erro ao criar authentication token: {}", ex.getMessage(), ex);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Setar no contexto do Spring Security
            try {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //logger.debug("Autenticação setada no SecurityContext para usuário: {}", username);
            } catch (Exception ex) {
                //logger.error("Erro ao setar autenticação no SecurityContext: {}", ex.getMessage(), ex);
                filterChain.doFilter(request, response);
                return;
            }
            
        } catch (Exception ex) {
            // CRÍTICO: Capturar QUALQUER exceção não tratada
            //logger.error("ERRO INESPERADO no filtro JWT: {}", ex.getMessage(), ex);
            //logger.error("Tipo de exceção: {}", ex.getClass().getName());
            //logger.error("Stack trace:", ex);
            
            // NÃO propagar exceção, apenas continuar
        }
        
        // SEMPRE continuar chain de filtros
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("Erro ao executar filterChain: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
    
    /**
     * Verifica se o endpoint é público
     */
    private boolean isPublicEndpoint(String path) {
        boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
        // if (isPublic) {
        //     //logger.trace("Endpoint {} é público", path);
        // }
        return isPublic;
    }
    
    /**
     * Extrai JWT do header Authorization: Bearer <token>
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            
            if (StringUtils.hasText(bearerToken)) {
                // logger.trace("Authorization header encontrado: {}", 
                //            bearerToken.substring(0, Math.min(20, bearerToken.length())) + "...");
                
                if (bearerToken.startsWith("Bearer ")) {
                    String token = bearerToken.substring(7);
                    // logger.trace("Token extraído (primeiros 20 chars): {}...", 
                    //            token.substring(0, Math.min(20, token.length())));
                    return token;
                } //else {
                    //ogger.warn("Authorization header não começa com 'Bearer '");
                //}
            } //else {
                //logger.trace("Nenhum Authorization header encontrado");
            //}
        } catch (Exception ex) {
           // logger.error("Erro ao extrair JWT do request: {}", ex.getMessage());
        }
        
        return null;
    }
}
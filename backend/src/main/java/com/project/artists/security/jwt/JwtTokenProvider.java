package com.project.artists.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Provedor de tokens JWT
 * Responsável por gerar, validar e extrair informações dos tokens
 */
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs; // 300000 = 5 minutos
    
    @Value("${app.jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs; // 86400000 = 24 horas
    
    /**
     * Gera token de acesso (5 minutos)
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), jwtExpirationMs);
    }
    
    /**
     * Gera token de acesso a partir do username
     */
    public String generateAccessToken(String username) {
        return generateToken(username, jwtExpirationMs);
    }
    
    /**
     * Gera refresh token (24 horas)
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, jwtRefreshExpirationMs);
    }
    
    /**
     * Gera token genérico
     */
    private String generateToken(String username, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Extrai username do token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();
    }
    
    /**
     * Valida token JWT
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            
            return true;
        } catch (SecurityException ex) {
            System.err.println("Assinatura JWT invalida: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT invalido: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT nao suportado: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string esta vazia: " + ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Retorna tempo de expiração do access token em segundos
     */
    public long getExpirationInSeconds() {
        return jwtExpirationMs / 1000;
    }
}

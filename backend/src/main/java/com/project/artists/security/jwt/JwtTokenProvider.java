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
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

     /**
     * Gera token de acesso (5 minutos)
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername(), jwtExpirationMs, TYPE_ACCESS);
    }
     /**
     * Gera token de acesso a partir do username
     */
    public String generateAccessToken(String username) {
        return generateToken(username, jwtExpirationMs, TYPE_ACCESS);
    }
    /**
     * Gera refresh token (24 horas)
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, jwtRefreshExpirationMs, TYPE_REFRESH);
    }

    /**
     * Gera token genérico
     */
    private String generateToken(String username, long expirationMs, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_TOKEN_TYPE, type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
     /**
     * Valida token JWT
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);

            return true;

        } catch (ExpiredJwtException e) {
           // System.out.println("JWT EXPIRADO: " + e.getMessage());
        } catch (SecurityException e) {
            //System.out.println("ASSINATURA INVÁLIDA (secret mudou?): " + e.getMessage());
        } catch (MalformedJwtException e) {
            //System.out.println("JWT MALFORMADO (token truncado): " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
           // System.out.println("JWT INVÁLIDO: " + e.getMessage());
        }
        return false;
    }

     /**
     * Valida token refresh
     */
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) return false;

        try {
            Claims claims = getAllClaims(token);
            String type = claims.get(CLAIM_TOKEN_TYPE, String.class);
            return TYPE_REFRESH.equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrai username do token
     */
    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Extrai username do token
     */
    public Claims getAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     *  Retorna tempo de expiração
     */
    public long getExpirationInSeconds() {
        return jwtExpirationMs / 1000;
    }
}

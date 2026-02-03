package com.project.artists.security.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Cria e salva um refresh token no banco e retorna a STRING do token
     */
    public String createRefreshToken(String username) {

        // gera token aleatório
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID();

        // expiração
        Instant expiresAt = Instant.now().plusMillis(refreshExpirationMs);

        // cria entidade (SEM builder)
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUsername(username);
        refreshTokenEntity.setToken(token);
        refreshTokenEntity.setExpiresAt(expiresAt);
        refreshTokenEntity.setRevoked(false);

        // salva no banco
        refreshTokenRepository.save(refreshTokenEntity);

        // retorna string
        return token;
    }
}

package io.darpan.user_starter.service;

import io.darpan.user_starter.model.BlacklistedToken;
import io.darpan.user_starter.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class TokenBlacklist {
    private final BlacklistedTokenRepository tokenRepository;
    private static final String SECRET = "Rk9uTktMTXJyYmZzMTJwc0lpc0kzV1ZKZ2VhZ3pxZDc=";

    public TokenBlacklist(BlacklistedTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void add(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration != null) {
                BlacklistedToken blacklistedToken = new BlacklistedToken();
                blacklistedToken.setToken(token);
                blacklistedToken.setExpiryDate(expiration.toInstant());
                BlacklistedToken blacklistedToken1 = tokenRepository.save(blacklistedToken);
                log.error("Blacklisted token: {}", blacklistedToken1);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", token, e);
        }
    }

    public boolean isBlacklisted(String token) {
        return tokenRepository.findByToken(token).isPresent();
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }
}
package io.darpan.user_starter.serviceimpl;

import io.darpan.user_starter.model.BlacklistedToken;
import io.darpan.user_starter.repository.BlacklistedTokenRepository;
import io.darpan.user_starter.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final BlacklistedTokenRepository tokenRepository;
    private final SecretKey signingKey;

    public TokenBlacklistServiceImpl(BlacklistedTokenRepository tokenRepository, SecretKey signingKey) {
        this.tokenRepository = tokenRepository;
        this.signingKey = signingKey;
    }

    @Transactional
    public void add(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration != null) {
                BlacklistedToken blacklistedToken = new BlacklistedToken();
                blacklistedToken.setToken(token);
                blacklistedToken.setExpiryDate(expiration.toInstant());
                BlacklistedToken savedToken = tokenRepository.save(blacklistedToken);
                log.info("Token blacklisted successfully: {}", savedToken.getId());
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", token, e);
            throw new RuntimeException("Failed to process logout", e);
        }
    }

    public boolean isBlacklisted(String token) {
        return tokenRepository.findByToken(token).isPresent();
    }
}
package io.darpan.user_starter.schedulers;

import io.darpan.user_starter.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ExpiredTokenScheduler {

    private final BlacklistedTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }
}

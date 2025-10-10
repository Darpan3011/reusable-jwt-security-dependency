package io.darpan.user_starter.repository;

import io.darpan.user_starter.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);

    @Query("DELETE FROM BlacklistedToken t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
}

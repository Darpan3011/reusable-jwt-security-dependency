package io.darpan.user_starter.config;

import io.darpan.user_starter.filter.JwtAuthFilter;
import io.darpan.user_starter.helper.JwtTokenService;
import io.darpan.user_starter.repository.BlacklistedTokenRepository;
import io.darpan.user_starter.service.TokenBlacklist;
import io.darpan.user_starter.service.UserV2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStarterAutoConfiguration {

    @Bean
    public JwtTokenService jwtTokenService() {
        return new JwtTokenService();
    }

    @Bean
    public TokenBlacklist tokenBlacklist(BlacklistedTokenRepository blacklistedTokenRepository) {
        return new TokenBlacklist(blacklistedTokenRepository);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(
            JwtTokenService jwtTokenService,
            UserV2Service userDetailsService,
            TokenBlacklist tokenBlacklist) {
        return new JwtAuthFilter(jwtTokenService, userDetailsService, tokenBlacklist);
    }
}

package io.darpan.user_starter.config;

import io.darpan.user_starter.config.properties.SecurityProperties;
import io.darpan.user_starter.filter.JwtAuthFilter;
import io.darpan.user_starter.helper.JwtTokenProvider;
import io.darpan.user_starter.repository.BlacklistedTokenRepository;
import io.darpan.user_starter.serviceimpl.TokenBlacklistServiceImpl;
import io.darpan.user_starter.serviceimpl.UserV2ServiceImpl;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class UserStarterAutoConfiguration {

    @Bean
    public SecretKey jwtSigningKey(SecurityProperties securityProperties) {
        SecretKey secretKey = Keys.hmacShaKeyFor(
                securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
        log.error("{}", secretKey);
        return secretKey;
    }

    @Bean
    public JwtTokenProvider jwtTokenService(SecurityProperties securityProperties, SecretKey secretKey) {
        return new JwtTokenProvider(securityProperties, secretKey);
    }

    @Bean
    public TokenBlacklistServiceImpl tokenBlacklist(BlacklistedTokenRepository blacklistedTokenRepository, SecretKey secretKey) {
        return new TokenBlacklistServiceImpl(blacklistedTokenRepository, secretKey);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(
            JwtTokenProvider jwtTokenProvider,
            UserV2ServiceImpl userDetailsService,
            TokenBlacklistServiceImpl tokenBlacklistServiceImpl) {
        return new JwtAuthFilter(jwtTokenProvider, userDetailsService, tokenBlacklistServiceImpl);
    }
}

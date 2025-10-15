package io.darpan.user_starter.service;

import java.util.Map;

public interface TokenProvider {
    String generateToken(String username, Map<String, Object> extraClaims);
    String extractUsername(String token);
    boolean validateToken(String token);
    boolean isTokenExpired(String token);
}

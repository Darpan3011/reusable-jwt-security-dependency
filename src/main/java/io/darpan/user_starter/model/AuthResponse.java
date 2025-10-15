package io.darpan.user_starter.model;

public record AuthResponse(String message, String token, String tokenType, long expiresIn) {
}

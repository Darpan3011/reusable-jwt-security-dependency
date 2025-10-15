package io.darpan.user_starter.model;

public record UserProfileResponse(Long id, String username, String role, boolean enabled, boolean accountNonLocked) {
}

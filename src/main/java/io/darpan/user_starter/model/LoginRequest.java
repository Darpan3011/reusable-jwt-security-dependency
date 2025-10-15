package io.darpan.user_starter.model;

import jakarta.validation.constraints.NotBlank;

// DTOs
public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}

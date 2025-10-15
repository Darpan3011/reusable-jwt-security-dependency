package io.darpan.user_starter.controller;

import io.darpan.user_starter.config.properties.SecurityProperties;
import io.darpan.user_starter.helper.JwtTokenProvider;
import io.darpan.user_starter.model.*;
import io.darpan.user_starter.serviceimpl.TokenBlacklistServiceImpl;
import io.darpan.user_starter.serviceimpl.UserV2ServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class UserV2Controller {

    private final AuthenticationManager authManager;
    @Qualifier("jwtTokenService")
    private final JwtTokenProvider jwtService;
    private final UserV2ServiceImpl userV2ServiceImpl;
    private final SecurityProperties securityProperties;
    private final TokenBlacklistServiceImpl tokenBlacklistServiceImpl;

    public UserV2Controller(
            AuthenticationManager authManager,
            @Qualifier("jwtTokenService") JwtTokenProvider jwtService,
            UserV2ServiceImpl userV2ServiceImpl,
            SecurityProperties securityProperties,
            TokenBlacklistServiceImpl tokenBlacklistServiceImpl
    ) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userV2ServiceImpl = userV2ServiceImpl;
        this.securityProperties = securityProperties;
        this.tokenBlacklistServiceImpl = tokenBlacklistServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody LoginRequest user) {
        userV2ServiceImpl.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse("User registered successfully", null, null, securityProperties.getJwt().getTokenValidity().toSeconds()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Map<String, Object> claims = new HashMap<>();

        // Add user details to claims
        claims.put("userId", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        String token = jwtService.generateToken(userDetails.getUsername(), claims);

        return ResponseEntity.ok(new AuthResponse(
                "Login successful",
                token,
                "Bearer",
                securityProperties.getJwt().getTokenValidity().toSeconds()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            tokenBlacklistServiceImpl.add(jwtToken);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserV2 user = userV2ServiceImpl.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled(),
                user.isAccountNonLocked()
        ));
    }
}


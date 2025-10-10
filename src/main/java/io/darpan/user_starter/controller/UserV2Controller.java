package io.darpan.user_starter.controller;

import io.darpan.user_starter.helper.JwtTokenService;
import io.darpan.user_starter.model.UserV2;
import io.darpan.user_starter.service.TokenBlacklist;
import io.darpan.user_starter.service.UserV2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserV2Controller {

    private final AuthenticationManager authManager;
    private final UserV2Service userV2Service;
    private final JwtTokenService jwtService;
    private final TokenBlacklist tokenBlacklist;

    public UserV2Controller(AuthenticationManager authManager, UserV2Service userV2Service, JwtTokenService jwtService, TokenBlacklist tokenBlacklist) {
        this.authManager = authManager;
        this.userV2Service = userV2Service;
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserV2 user) {
        userV2Service.createUser(user);
        return ResponseEntity.ok(String.format("User registered successfully: %s", user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserV2 user) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        return ResponseEntity.ok(Map.of("Token", jwtService.generateToken(auth.getName())));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        tokenBlacklist.add(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
}


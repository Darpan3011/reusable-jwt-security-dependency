package com.darpan.starter.security.serviceimpl;

import com.darpan.starter.security.jwt.JwtTokenProvider;
import com.darpan.starter.security.model.Role;
import com.darpan.starter.security.model.Token;
import com.darpan.starter.security.model.User;
import com.darpan.starter.security.repository.RoleRepository;
import com.darpan.starter.security.repository.TokenRepository;
import com.darpan.starter.security.repository.UserRepository;
import com.darpan.starter.security.service.AuthService;
import com.darpan.starter.security.service.dto.AuthResponse;
import com.darpan.starter.security.service.dto.LoginRequest;
import com.darpan.starter.security.service.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepo;

    public AuthServiceImpl(UserRepository userRepo, TokenRepository tokenRepo, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, RoleRepository roleRepository) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.roleRepo = roleRepository;
    }

    @Override
    @Transactional
    public User register(RegisterRequest req) {
        Role defaultRole = roleRepo.findByName("USER").orElseGet(() -> roleRepo.save(new Role("USER")));
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRoles(new HashSet<>(Collections.singleton(defaultRole))); // defaultRole = USER entity
        return userRepo.save(u);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) throw new RuntimeException("Invalid credentials");
        tokenRepo.deactivateOldTokens(user.getId());

        String access = tokenProvider.generateAccessToken(user.getUsername(), user.getId());
        String refresh = tokenProvider.generateRefreshToken(user.getUsername(), user.getId());

        Token token = new Token();
        token.setUserId(user.getId());
        token.setAccessToken(access);
        token.setRefreshToken(refresh);
        token.setAccessTokenExpiry(Instant.now().plusMillis(tokenProvider.getAccessExpiryMillis()));
        token.setRefreshTokenExpiry(Instant.now().plusMillis(tokenProvider.getRefreshExpiryMillis()));
        token.setActive(true);

        tokenRepo.save(token);
        return new AuthResponse(access, refresh, tokenProvider.getAccessExpiryMillis() / 1000);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        Token oldToken = tokenRepo.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (!oldToken.isActive()) throw new RuntimeException("Inactive token");
        if (!tokenProvider.validateToken(refreshToken)) {
            oldToken.setActive(false);
            tokenRepo.save(oldToken);
            throw new RuntimeException("Expired refresh token");
        }

        // parse username from refresh token
        String username = tokenProvider.getUsername(refreshToken);
        Long uid = oldToken.getUserId();

        // generate new tokens
        String newAccess = tokenProvider.generateAccessToken(username, uid);
        String newRefresh = tokenProvider.generateRefreshToken(username, uid);

        // deactivate the old one
        oldToken.setActive(false);
        tokenRepo.save(oldToken);

        // create a new entry
        Token newToken = new Token();
        newToken.setUserId(uid);
        newToken.setAccessToken(newAccess);
        newToken.setRefreshToken(newRefresh);
        newToken.setAccessTokenExpiry(Instant.now().plusMillis(tokenProvider.getAccessExpiryMillis()));
        newToken.setRefreshTokenExpiry(Instant.now().plusMillis(tokenProvider.getRefreshExpiryMillis()));
        newToken.setActive(true);
        tokenRepo.save(newToken);

        return new AuthResponse(newAccess, newRefresh, tokenProvider.getAccessExpiryMillis() / 1000);
    }

    // removed empty key method; provider is the single source for JWT parsing

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}

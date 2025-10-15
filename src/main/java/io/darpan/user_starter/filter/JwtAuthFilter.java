package io.darpan.user_starter.filter;

import io.darpan.user_starter.service.TokenBlacklistService;
import io.darpan.user_starter.service.TokenProvider;
import io.darpan.user_starter.service.UserV2Service;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter implements AuthenticationFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;
    private final UserV2Service userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {

        String authHeader = request.getHeader(AUTH_HEADER);
        return authHeader == null || !authHeader.startsWith(TOKEN_PREFIX);
    }

    @Override
    public void handleInvalidAuthentication(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (tokenBlacklistService.isBlacklisted(token) || !tokenProvider.validateToken(token)) {
            handleInvalidAuthentication(request, response);
            return;
        }

        String username = tokenProvider.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        return authHeader.substring(TOKEN_PREFIX.length());
    }
}

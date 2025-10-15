package io.darpan.user_starter.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationFilter extends Filter {
    boolean shouldNotFilter(HttpServletRequest request);
    void handleInvalidAuthentication(HttpServletRequest request, HttpServletResponse response);
}

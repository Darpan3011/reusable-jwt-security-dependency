package io.darpan.user_starter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private List<String> publicEndpoints = new ArrayList<>();
    private boolean csrfEnabled = false;
    private boolean corsEnabled = true;

    @Data
    public static class Jwt {
        private String secret = "Rk9uTktMTXJyYmZzMTJwc0lpc0kzV1ZKZ2VhZ3pxZDc="; // Default value, should be overridden
        private Duration tokenValidity = Duration.ofHours(24);
        private String issuer = "user-starter";
        private String audience = "user-client";
        private String tokenPrefix = "Bearer ";
        private String headerName = "Authorization";
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("*");
        private List<String> allowedMethods = List.of("*");
        private List<String> allowedHeaders = List.of("*");
        private List<String> exposedHeaders = List.of("*");
        private boolean allowCredentials = true;
        private Long maxAge = 3600L;
    }
}

package org.wilsonks.slotauthservice.config;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Slf4j
public class JwtProperties {
    private String secret;
    private Long playerSessionExpiration;
    private Long employeeSessionExpiration;
    private Long refreshExpiration;
    private String issuer;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.length() < 32) {
            log.error("JWT secret must be at least 32 characters long");
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        if (playerSessionExpiration == null || playerSessionExpiration <= 0) {
            log.error("Player session expiration must be a positive number");
            throw new IllegalArgumentException("Player session expiration must be a positive number");
        }
        if (employeeSessionExpiration == null || employeeSessionExpiration <= 0) {
            log.error("Employee session expiration must be a positive number");
            throw new IllegalArgumentException("Employee session expiration must be a positive number");
        }
        if (refreshExpiration == null || refreshExpiration <= 0) {
            log.error("Refresh token expiration must be a positive number");
            throw new IllegalArgumentException("Refresh token expiration must be a positive number");
        }
        if (issuer == null || issuer.isEmpty()) {
            log.error("Issuer must not be empty");
            throw new IllegalArgumentException("Issuer must not be empty");
        }

        log.info("✅ JWT Properties validated successfully");
        log.info("✅ JWT Secret: {}", secret);
        log.info("✅ Player Session Expiration: {} ms", playerSessionExpiration);
        log.info("✅ Employee Session Expiration: {} ms", employeeSessionExpiration);
        log.info("✅ Refresh Token Expiration: {} ms", refreshExpiration);
        log.info("✅ Issuer: {}", issuer);
    }
}

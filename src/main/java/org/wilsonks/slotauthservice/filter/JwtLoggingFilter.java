package org.wilsonks.slotauthservice.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.wilsonks.slotauthservice.service.JwtService;

import java.io.IOException;

@Slf4j
@Component
public class JwtLoggingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtLoggingFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (authorization != null && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            try {

                Claims claims = jwtService.extractClaims(token);

                log.info("===== JWT TOKEN =====");
                log.info("Subject   : {}", claims.getSubject());
                log.info("Issuer    : {}", claims.getIssuer());
                log.info("Type      : {}", claims.get("type"));
                log.info("Role      : {}", claims.get("role"));
                log.info("Issued At : {}", claims.getIssuedAt());
                log.info("Expires At: {}", claims.getExpiration());
                log.info("=====================");

            } catch (Exception ex) {

                log.warn("Invalid JWT : {}", ex.getMessage());

            }
        }

        filterChain.doFilter(request, response);
    }
}
package org.wilsonks.slotauthservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.wilsonks.slotauthservice.config.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {

        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.jwtProperties = jwtProperties;
    }

    private String generateToken(String subject, String type, String role, long expirationTime) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationTime);

        return Jwts.builder()
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .claim("type", type)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String generateEmployeeToken(String uid, String role) {
        return generateToken(uid, "Employee", role, jwtProperties.getEmployeeSessionExpiration());
    }

    public String generatePlayerToken(String uid) {
        return generateToken(uid, "Player", "PLAYER", jwtProperties.getPlayerSessionExpiration());
    }

    // ---------------------------------------------------------------------
    // Parsing
    // ---------------------------------------------------------------------

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ---------------------------------------------------------------------
    // Claim extraction
    // ---------------------------------------------------------------------

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isEmployeeToken(String token) {
        return "employee".equals(extractType(token));
    }

    public boolean isPlayerToken(String token) {
        return "player".equals(extractType(token));
    }

    public boolean validate(String token) {
        try {

            Claims claims = extractClaims(token);

            return !claims.getExpiration().before(new Date());

        } catch (JwtException | IllegalArgumentException ex) {

            return false;

        }
    }

}

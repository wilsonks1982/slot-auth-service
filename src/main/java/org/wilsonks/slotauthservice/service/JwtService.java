package org.wilsonks.slotauthservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    //A secret key used for signing and verifying JWT tokens.
    // In a real application, this should be stored securely and not hard-coded.
    public static final String SECRET_KEY = "your-super-secret-key-that-is-at-least-32-bytes-long!";

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(String subject, String type, String role, long expirationTime) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationTime);

        return Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateEmployeeToken(String uid, String role, long expirationTime) {
        return generateToken(uid, "employee", role, expirationTime);
    }

    public String generatePlayerToken(String uid, long expirationTime) {
        return generateToken(uid, "player", "PLAYER", expirationTime);
    }

}

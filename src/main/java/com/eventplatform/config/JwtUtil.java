package com.eventplatform.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final Key signingKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs,
            Environment env) {

        String resolvedSecret = secret == null ? "" : secret;

        if (resolvedSecret.length() < 32) {
            boolean devProfile = false;

            try {
                devProfile = env != null &&
                        env.getActiveProfiles() != null &&
                        java.util.Arrays.stream(env.getActiveProfiles())
                                .anyMatch(p -> p.equalsIgnoreCase("dev")
                                        || p.equalsIgnoreCase("development"));
            } catch (Exception ignored) {}

            if (devProfile) {
                resolvedSecret = "dev-fallback-secret-0123456789abcdef012345";
                log.warn("""
                        JWT secret is missing or too short.
                        Using DEVELOPMENT fallback secret.
                        ⚠ DO NOT use this in production!
                        """);
            } else {
                throw new IllegalArgumentException(
                        "jwt.secret must be configured and at least 32 characters long"
                );
            }
        }

        this.signingKey =
                Keys.hmacShaKeyFor(resolvedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;

        log.info("JwtUtil initialized (expiration={} ms)", expirationMs);
    }


    // TOKEN GENERATION
    public String generateToken(String subject, Map<String, Object> claims) {

        Objects.requireNonNull(subject, "JWT subject cannot be null");

        if (claims == null || !claims.containsKey("role")) {
            throw new IllegalArgumentException("JWT must contain a 'role' claim");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // TOKEN VALIDATION
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired at {}", ex.getClaims().getExpiration());
            return false;

        } catch (JwtException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }


    // CLAIM EXTRACTION
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        String role = getClaims(token).get("role", String.class);
        if (role == null) {
            throw new RuntimeException("Role missing in JWT");
        }
        return role;
    }

    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }
}

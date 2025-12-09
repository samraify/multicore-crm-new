package com.multicore.crm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.multicore.crm.entity.User;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT Utility for token creation and parsing.
 * Expects a Base64-encoded secret (64 bytes recommended for HS512) in application.properties:
 *   jwt.secret=BASE64_STRING
 *
 * Also expects:
 *   jwt.expiration=<millis>
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret; // must be provided (prefer env var)

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    // cached signing key (initialized once)
    private SecretKey signingKey;

    @PostConstruct
    private void init() {
        // Build signing key from configured secret and validate length for HS512
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException ex) {
            // If not Base64, fallback to UTF-8 bytes but log strong warning.
            log.warn("jwt.secret is not valid Base64. Falling back to raw UTF-8 bytes (not recommended for production).");
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }

        // HS512 requires key size >= 512 bits (64 bytes).
        if (keyBytes.length < 64) {
            String msg = String.format(
                    "Configured JWT secret is too short (%d bytes). HS512 requires at least 64 bytes. " +
                            "Please provide a 64-byte (or longer) Base64-encoded secret in 'jwt.secret'.",
                    keyBytes.length);
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        signingKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT signing key initialized (algorithm=HS512).");
    }

    private SecretKey getSigningKey() {
        // Use cached signingKey from init()
        return signingKey;
    }

    /**
     * Generate JWT token with necessary claims for multi-tenant SaaS.
     * Note: User.getBusiness() is used here for simplicity. If your model supports multiple memberships,
     * adapt generation to use the selected membership (UserBusiness) instead.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        if (user.getId() != null) claims.put("userId", user.getId());
        if (user.getEmail() != null) claims.put("email", user.getEmail());
        if (user.getFullName() != null) claims.put("fullName", user.getFullName());

        // businessId / tenantId
        if (user.getBusiness() != null && user.getBusiness().getId() != null) {
            claims.put("businessId", user.getBusiness().getId());
            claims.put("tenantId", user.getBusiness().getId());
        } else {
            claims.put("businessId", null);
            claims.put("tenantId", null);
        }

        // Roles as a list claim (preferred to comma-separated string)
        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles().stream()
                .map(r -> r.getRoleName().toString())
                .collect(Collectors.toList());
        claims.put("roles", roles);

        // Subject - use email if available, otherwise user id string
        String subject = user.getEmail() != null ? user.getEmail() : String.valueOf(user.getId());

        return createToken(claims, subject);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            return userId != null ? ((Number) userId).longValue() : null;
        });
    }

    public Long extractBusinessId(String token) {
        return extractClaim(token, claims -> {
            Object businessId = claims.get("businessId");
            return businessId != null ? ((Number) businessId).longValue() : null;
        });
    }

    /**
     * Extract roles claim as a List<String>. This method tolerates two shapes:
     *  - an actual List in the claims (preferred),
     *  - a single comma-separated string (legacy).
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rolesObj = claims.get("roles");
            if (rolesObj == null) return List.of();
            if (rolesObj instanceof List) {
                return ((List<?>) rolesObj).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            } else {
                // fallback: comma-separated string
                String s = rolesObj.toString();
                return List.of(s.split("\\s*,\\s*"));
            }
        });
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT token", e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed", e);
            return false;
        }
    }
}

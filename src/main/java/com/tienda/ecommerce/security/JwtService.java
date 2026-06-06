package com.tienda.ecommerce.security;

import com.tienda.ecommerce.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // RECOMENDACIÓN: Una clave de 32 bytes o más para algoritmos HMAC-SHA
    private static final String SECRET = "ESTA_ES_UNA_CLAVE_SECRETA_DE_32_BYTES_MINIMO_123456_SUPER_SECURE";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 horas de validez

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Inyecta la lista de roles dinámicos de Neon en los claims del JWT
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Convertimos el Set de Enums a una lista de Strings (Ej: ["ROLE_USER", "ROLE_ADMIN"])
        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        claims.put("roles", rolesList);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            boolean emailCoincide = email.equals(userDetails.getUsername());
            boolean tokenNoExpirado = !isTokenExpired(token);

            return (emailCoincide && tokenNoExpirado);
        } catch (Exception e) {
            System.out.println("[JWT SERVICE] ❌ Error validando token (Firma corrupta, clave cambiada o expirado): " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
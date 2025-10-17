package com.classreport.classreport.service;

import com.classreport.classreport.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Token parse edilərkən xəta baş verdi: " + e.getMessage());
        }
    }

    // ✅ DÜZƏLDİLDİ: YALNIZ BİR generateToken METODU OLSUN
    public String generateToken(UserEntity user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole().name());
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256) // ✅ COMMENT SİLİNDİ
                    .compact();

        } catch (Exception e) {
            log.error("Token generation error: {}", e.getMessage());
            throw new RuntimeException("Token generation failed");
        }
    }
    // Refresh Token yaratmaq
    public String generateRefreshToken(UserEntity user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("tokenType", "refresh");
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();

        } catch (Exception e) {
            log.error("Refresh token generation error: {}", e.getMessage());
            throw new RuntimeException("Refresh token generation failed");
        }
    }

    // Refresh Tokeni validate etmək
    public boolean isRefreshTokenValid(String token) {
        try {
            if (!validateToken(token)) {
                return false;
            }

            final Claims claims = extractAllClaims(token);
            String tokenType = claims.get("tokenType", String.class);
            return "refresh".equals(tokenType);

        } catch (Exception e) {
            log.error("Refresh token validation error: {}", e.getMessage());
            return false;
        }
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        try {
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Token yaradılarkən xəta baş verdi: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims.containsKey("userId")) {
                return claims.get("userId", Long.class);
            }
            try {
                return Long.parseLong(claims.getSubject());
            } catch (NumberFormatException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims.containsKey("role")) {
                return claims.get("role", String.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public long getRemainingTime(String token) {
        try {
            final Date expiration = extractExpiration(token);
            final Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
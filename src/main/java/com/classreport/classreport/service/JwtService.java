package com.classreport.classreport.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // Secret keyi cache etmək üçün
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Bütün claims-ləri extract et
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

    // Access token yaratmaq
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // Refresh token yaratmaq
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    // Token qurmaq (ümumi metod)
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

    // Tokenin etibarlı olub olmadığını yoxlamaq
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Tokenin müddətinin bitib-bitmədiyini yoxlamaq
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Əgər xəta baş verərsə, tokeni expired say
        }
    }

    // Tokenin expiration tarixini almaq
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Tokendən userId almaq (əlavə edilmiş claim əsasında)
    public Long extractUserId(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            // Əgər tokenə userId əlavə edilibsə
            if (claims.containsKey("userId")) {
                return claims.get("userId", Long.class);
            }
            // Əgər userId claimi yoxdursa, subject-dən çalışaq (əgər subject userId-dirsə)
            try {
                return Long.parseLong(claims.getSubject());
            } catch (NumberFormatException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Tokendən role almaq
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

    // Tokenin qalan müddətini almaq (millisaniyələrlə)
    public long getRemainingTime(String token) {
        try {
            final Date expiration = extractExpiration(token);
            final Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    // Tokeni validate etmək (xəta atmayan versiya)
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Əgər xəta atmasa, token validdir
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Authorization header-dan tokeni çıxarmaq
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
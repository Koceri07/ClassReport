package com.classreport.classreport.ServicesTests

import com.classreport.classreport.entity.UserEntity
import com.classreport.classreport.model.enums.Role
import com.classreport.classreport.service.JwtService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import spock.lang.Specification
import spock.lang.Subject

import java.security.Key
import java.time.LocalDate
import java.util.Date

class JwtServiceTest extends Specification {

    @Subject
    def jwtService = new JwtService()

    def userEntity
    def secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    def jwtExpiration = 86400000L // 24 saat
    def refreshExpiration = 604800000L // 7 gün

    def setup() {
        // Reflection ilə private field-ləri set etmək
        def secretField = jwtService.class.getDeclaredField("secretKey")
        secretField.setAccessible(true)
        secretField.set(jwtService, secretKey)

        def expirationField = jwtService.class.getDeclaredField("jwtExpiration")
        expirationField.setAccessible(true)
        expirationField.set(jwtService, jwtExpiration)

        def refreshField = jwtService.class.getDeclaredField("refreshExpiration")
        refreshField.setAccessible(true)
        refreshField.set(jwtService, refreshExpiration)

        userEntity = new UserEntity()
        userEntity.setId(1L)
        userEntity.setEmail("test@example.com")
        userEntity.setRole(Role.TEACHER)
    }

    def "extractUsername should return username from valid token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def result = jwtService.extractUsername(token)

        then:
        result == userEntity.getEmail()
    }

    def "generateToken should create valid token with claims"() {
        when:
        def token = jwtService.generateToken(userEntity)

        then:
        token != null
        !token.isEmpty()

        when:
        def username = jwtService.extractUsername(token)
        def userId = jwtService.extractUserId(token)
        def role = jwtService.extractRole(token)

        then:
        username == userEntity.getEmail()
        userId == userEntity.getId()
        role == userEntity.getRole().name()
    }

    def "generateRefreshToken should create valid refresh token"() {
        when:
        def refreshToken = jwtService.generateRefreshToken(userEntity)

        then:
        refreshToken != null
        !refreshToken.isEmpty()
        jwtService.isRefreshTokenValid(refreshToken)
    }

    def "isRefreshTokenValid should return true for valid refresh token"() {
        given:
        def refreshToken = jwtService.generateRefreshToken(userEntity)

        when:
        def isValid = jwtService.isRefreshTokenValid(refreshToken)

        then:
        isValid
    }

    def "isRefreshTokenValid should return false for invalid token"() {
        given:
        def invalidToken = "invalid.token.here"

        when:
        def isValid = jwtService.isRefreshTokenValid(invalidToken)

        then:
        !isValid
    }

    def "isRefreshTokenValid should return false for expired refresh token"() {
        given:
        def expiredToken = generateExpiredToken(userEntity)

        when:
        def isValid = jwtService.isRefreshTokenValid(expiredToken)

        then:
        !isValid
    }

    def "isTokenValid should return true for valid token"() {
        given:
        def token = jwtService.generateToken(userEntity)
        def userDetails = Mock(org.springframework.security.core.userdetails.UserDetails)
        userDetails.getUsername() >> userEntity.getEmail()

        when:
        def isValid = jwtService.isTokenValid(token, userDetails)

        then:
        isValid
    }

    def "isTokenValid should return false for invalid username"() {
        given:
        def token = jwtService.generateToken(userEntity)
        def userDetails = Mock(org.springframework.security.core.userdetails.UserDetails)
        userDetails.getUsername() >> "different@example.com"

        when:
        def isValid = jwtService.isTokenValid(token, userDetails)

        then:
        !isValid
    }

    def "isTokenValid should return false for expired token"() {
        given:
        def expiredToken = generateExpiredToken(userEntity)
        def userDetails = Mock(org.springframework.security.core.userdetails.UserDetails)
        userDetails.getUsername() >> userEntity.getEmail()

        when:
        def isValid = jwtService.isTokenValid(expiredToken, userDetails)

        then:
        !isValid
    }

    def "extractUserId should return user id from token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def userId = jwtService.extractUserId(token)

        then:
        userId == userEntity.getId()
    }

    def "extractRole should return role from token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def role = jwtService.extractRole(token)

        then:
        role == userEntity.getRole().name()
    }

    def "extractRole should return null when role claim not present"() {
        given:
        def token = generateTokenWithoutRole(userEntity)

        when:
        def role = jwtService.extractRole(token)

        then:
        role == null
    }

    def "validateToken should return true for valid token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def isValid = jwtService.validateToken(token)

        then:
        isValid
    }

    def "validateToken should return false for expired token"() {
        given:
        def expiredToken = generateExpiredToken(userEntity)

        when:
        def isValid = jwtService.validateToken(expiredToken)

        then:
        !isValid
    }

    def "validateToken should return false for invalid token"() {
        given:
        def invalidToken = "invalid.token.here"

        when:
        def isValid = jwtService.validateToken(invalidToken)

        then:
        !isValid
    }

    def "getRemainingTime should return positive value for valid token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def remainingTime = jwtService.getRemainingTime(token)

        then:
        remainingTime > 0
    }

    def "getRemainingTime should return zero for expired token"() {
        given:
        def expiredToken = generateExpiredToken(userEntity)

        when:
        def remainingTime = jwtService.getRemainingTime(expiredToken)

        then:
        remainingTime <= 0
    }

    def "extractTokenFromHeader should return token when valid header"() {
        given:
        def authHeader = "Bearer valid.token.here"

        when:
        def token = jwtService.extractTokenFromHeader(authHeader)

        then:
        token == "valid.token.here"
    }

    def "extractTokenFromHeader should return null when invalid header"() {
        given:
        def authHeader = "InvalidHeader"

        when:
        def token = jwtService.extractTokenFromHeader(authHeader)

        then:
        token == null
    }

    def "extractTokenFromHeader should return null when null header"() {
        when:
        def token = jwtService.extractTokenFromHeader(null)

        then:
        token == null
    }

    def "extractClaim should return specific claim from token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def email = jwtService.extractClaim(token, { claims -> claims.get("email", String.class) })

        then:
        email == userEntity.getEmail()
    }

    def "isTokenExpired should return false for valid token"() {
        given:
        def token = jwtService.generateToken(userEntity)

        when:
        def isExpired = jwtService.extractExpiration(token).before(new Date())

        then:
        !isExpired
    }

    // Helper methods
    private String generateExpiredToken(UserEntity user) {
        def key = Keys.hmacShaKeyFor(secretKey.bytes)

        return Jwts.builder()
                .setClaims([
                        "role": user.getRole().name(),
                        "userId": user.getId(),
                        "email": user.getEmail()
                ])
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 100000))
                .setExpiration(new Date(System.currentTimeMillis() - 50000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }

    private String generateTokenWithoutRole(UserEntity user) {
        def key = Keys.hmacShaKeyFor(secretKey.bytes)

        return Jwts.builder()
                .setClaims([
                        "userId": user.getId(),
                        "email": user.getEmail()
                ])
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }
}
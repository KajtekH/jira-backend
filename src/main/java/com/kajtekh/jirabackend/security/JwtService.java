package com.kajtekh.jirabackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String secretKey = "b7a002a04fc471eca942e43d9f8974d2826991ccd165dfa0f940df5598704161";
    private static final long loginTokenExp = 1000L * 60 * 5;
    private static final long refreshTokenExp = 1000L * 60 * 60 *24 * 7;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtService(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User userDetails) {
        return buildToken(extraClaims, userDetails);
    }

    public String generateRefreshToken(User user) {
        return buildRefreshToken(user);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedToken = (String) redisTemplate.opsForValue().get("refresh_token:" + username);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public TokenResponse getTokenPayload(String token){
        try{
            return objectMapper.readValue(decodeTokenPayload(token), TokenResponse.class);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to decode token payload", e);
        }
    }

    public void storeRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
                "refresh_token:" + username,
                refreshToken,
                Duration.ofMillis(refreshTokenExp)
        );
    }

    public void revokeRefreshToken(String username) {
        redisTemplate.delete("refresh_token:" + username);
    }

    private byte[] decodeTokenPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return Base64.getDecoder().decode(parts[1].getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private String buildRefreshToken(User user) {
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User userDetails
    ) {
        extraClaims.put("email", userDetails.getEmail());
        extraClaims.put("username", userDetails.getUsername());
        extraClaims.put("role", userDetails.getRole().name());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + loginTokenExp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

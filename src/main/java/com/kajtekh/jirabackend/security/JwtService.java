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

    public JwtService(final ObjectMapper objectMapper, final RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(final User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(
            final Map<String, Object> extraClaims,
            final User userDetails) {
        return buildToken(extraClaims, userDetails);
    }

    public String generateRefreshToken(final User user) {
        return buildRefreshToken(user);
    }


    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(final String username, final String refreshToken) {
        final String storedToken = (String) redisTemplate.opsForValue().get("refresh_token:" + username);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public TokenResponse getTokenPayload(final String token){
        try{
            return objectMapper.readValue(decodeTokenPayload(token), TokenResponse.class);
        }
        catch (final Exception e){
            throw new RuntimeException("Failed to decode token payload", e);
        }
    }

    public void storeRefreshToken(final String username, final String refreshToken) {
        redisTemplate.opsForValue().set(
                "refresh_token:" + username,
                refreshToken,
                Duration.ofMillis(refreshTokenExp)
        );
    }

    public void revokeRefreshToken(final String username) {
        redisTemplate.delete("refresh_token:" + username);
    }

    private byte[] decodeTokenPayload(final String token) {
        final String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return Base64.getDecoder().decode(parts[1].getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private String buildRefreshToken(final User user) {
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildToken(
            final Map<String, Object> extraClaims,
            final User userDetails
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

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

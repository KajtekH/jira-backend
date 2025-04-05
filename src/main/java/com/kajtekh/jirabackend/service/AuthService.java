package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import com.kajtekh.jirabackend.model.auth.AuthenticationResponse;
import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.UserRepository;
import com.kajtekh.jirabackend.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.kajtekh.jirabackend.model.user.Role.USER;
import static com.kajtekh.jirabackend.security.TokenCookieBuilder.REFRESH_TOKEN_COOKIE;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Register method called with request: {}", request);

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .role(USER)
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByUsernameOrEmail(request.email()).orElseThrow();
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        jwtService.storeRefreshToken(user.getUsername(), refreshToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No cookies found");
        }
        final var refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token not found"))
                .getValue();
        var username = jwtService.extractUsername(refreshToken);

        if (!jwtService.validateRefreshToken(username, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        final var user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        final var accessToken = jwtService.generateToken(user);
        final var newRefreshToken = jwtService.generateRefreshToken(user);

        jwtService.revokeRefreshToken(username);
        jwtService.storeRefreshToken(username, newRefreshToken);
        return new AuthenticationResponse(accessToken, newRefreshToken);
    }

    public TokenResponse getTokenPayload(String token) {
        return jwtService.getTokenPayload(token);
    }

    public void logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No cookies found");
        }
        final var refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token not found"))
                .getValue();
        final var username = jwtService.extractUsername(refreshToken);
        jwtService.revokeRefreshToken(username);
    }
}

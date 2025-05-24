package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import com.kajtekh.jirabackend.model.auth.AuthenticationResponse;
import com.kajtekh.jirabackend.model.auth.RefreshResponse;
import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.security.JwtService;
import com.kajtekh.jirabackend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.kajtekh.jirabackend.model.user.Role.USER;
import static com.kajtekh.jirabackend.security.TokenCookieBuilder.REFRESH_TOKEN_COOKIE;

@Service
public class AuthFacade {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFacade.class);

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Cache cache;

    public AuthFacade(final PasswordEncoder passwordEncoder, final JwtService jwtService, final UserService userService, final AuthenticationManager authenticationManager, final Cache cache) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.cache = cache;
    }

    public AuthenticationResponse register(final RegisterRequest request) {
        final var user = User.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(USER)
                .password(passwordEncoder.encode(request.password()))
                .isActive(true)
                .build();
        userService.save(user);
        LOG.info("User registered successfully: {}", user.getUsername());
        final var accessToken = jwtService.generateToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse login(final AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        final var user = userService.getUserByUsernameOrEmail(request.email());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        final var accessToken = jwtService.generateToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        jwtService.storeRefreshToken(user.getUsername(), refreshToken);
        LOG.info("User logged in successfully: {}", user.getUsername());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No cookies found");
        }
        final var refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token not found"))
                .getValue();
        final var username = jwtService.extractUsername(refreshToken);

        if (!jwtService.validateRefreshToken(username, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        final var user = userService.getUserByUsernameOrEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        final var accessToken = jwtService.generateToken(user);
        final var newRefreshToken = jwtService.generateRefreshToken(user);

        jwtService.revokeRefreshToken(username);
        jwtService.storeRefreshToken(username, newRefreshToken);
        return new AuthenticationResponse(accessToken, newRefreshToken);
    }

    public TokenResponse getTokenPayload(final String token) {
        return jwtService.getTokenPayload(token);
    }

    public void logout(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("No cookies found");
        }
        final var refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token not found"))
                .getValue();
        final var username = jwtService.extractUsername(refreshToken);
        cache.evict(username);
        jwtService.revokeRefreshToken(username);
        LOG.info("User logged out successfully: {}", username);
    }

    public RefreshResponse getExp(final String token) {
        return new RefreshResponse(jwtService.getTokenPayload(token).exp());
    }
}

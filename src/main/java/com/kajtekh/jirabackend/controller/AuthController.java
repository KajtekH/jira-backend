package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import com.kajtekh.jirabackend.model.auth.RefreshResponse;
import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.security.TokenCookieBuilder;
import com.kajtekh.jirabackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    private final TokenCookieBuilder tokenCookieBuilder;

    public AuthController(final AuthService service, final TokenCookieBuilder tokenCookieBuilder) {
        this.service = service;
        this.tokenCookieBuilder = tokenCookieBuilder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody final RegisterRequest request) {
        final var response = service.register(request);
        final var accessToken = tokenCookieBuilder.buildAccessTokenCookie(response.accessToken());
        return ResponseEntity.ok()
                .header(SET_COOKIE, accessToken.toString())
                .build();
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final AuthenticationRequest request) {
        final var response = service.login(request);
        final var accessToken = tokenCookieBuilder.buildAccessTokenCookie(response.accessToken());
        final var refreshCookie = tokenCookieBuilder.buildRefreshTokenCookie(response.refreshToken());
        return ResponseEntity.ok()
                .header(SET_COOKIE, accessToken.toString())
                .header(SET_COOKIE, refreshCookie.toString())
                .body(service.getTokenPayload(response.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(final HttpServletRequest request) {
        final var response = service.refresh(request);
        final var accessToken = tokenCookieBuilder.buildAccessTokenCookie(response.accessToken());
        final var refreshCookie = tokenCookieBuilder.buildRefreshTokenCookie(response.refreshToken());
        return ResponseEntity.ok()
                .header(SET_COOKIE, accessToken.toString())
                .header(SET_COOKIE, refreshCookie.toString())
                .body(service.getExp(response.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletRequest request) {
        service.logout(request);
        final var accessToken = tokenCookieBuilder.buildExpiredAccessTokenCookie();
        final var refreshCookie = tokenCookieBuilder.buildExpiredRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(SET_COOKIE, accessToken.toString())
                .header(SET_COOKIE, refreshCookie.toString())
                .build();
    }


}
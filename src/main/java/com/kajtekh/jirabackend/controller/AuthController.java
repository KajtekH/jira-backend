package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.security.TokenCookieBuilder;
import com.kajtekh.jirabackend.service.AuthService;
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

    public AuthController(AuthService service, TokenCookieBuilder tokenCookieBuilder) {
        this.service = service;
        this.tokenCookieBuilder = tokenCookieBuilder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        final var response = service.register(request);
        final var cookie = tokenCookieBuilder.buildLoginTokenCookie(response.token());
        return ResponseEntity.ok()
                .header(SET_COOKIE, cookie.toString())
                .build();
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthenticationRequest request) {
        final var response = service.login(request);
        final var cookie = tokenCookieBuilder.buildLoginTokenCookie(response.token());
        return ResponseEntity.ok()
                .header(SET_COOKIE, cookie.toString())
                .body(service.getTokenPayload(response.token()));
    }

}
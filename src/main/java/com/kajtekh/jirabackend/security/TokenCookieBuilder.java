package com.kajtekh.jirabackend.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class TokenCookieBuilder {
    public static final String LOGIN_TOKEN_COOKIE = "tab_proj_login_token";
    public static final String REFRESH_TOKEN_COOKIE = "tab_proj_refresh_token";

    public ResponseCookie buildLoginTokenCookie(String token) {
        return ResponseCookie.from(LOGIN_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 5)
                .build();
    }

    public ResponseCookie buildRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 15)
                .build();
    }
}

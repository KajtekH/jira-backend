package com.kajtekh.jirabackend.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class TokenCookieBuilder {
    public static final String ACCESS_TOKEN_COOKIE = "tab_proj_access_token";
    public static final String REFRESH_TOKEN_COOKIE = "tab_proj_refresh_token";

    public ResponseCookie buildAccessTokenCookie(final String token) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 5)
                .build();
    }

    public ResponseCookie buildRefreshTokenCookie(final String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 15)
                .build();
    }

    public ResponseCookie buildExpiredAccessTokenCookie() {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
    }

    public ResponseCookie buildExpiredRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
    }
}

package com.kajtekh.jirabackend.security;

import com.kajtekh.jirabackend.service.UserService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.cache.Cache;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.kajtekh.jirabackend.security.TokenCookieBuilder.ACCESS_TOKEN_COOKIE;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public final JwtService jwtService;
    private final UserService userService;
    private final Cache cache;

    public JwtAuthenticationFilter(final JwtService jwtService, final UserService userService, final Cache cache) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.cache = cache;
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain filterChain)
            throws ServletException, IOException {
        final var jwt = extractJwtFromCookie(request);
        if(jwt==null){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final var userName = jwtService.extractUsername(jwt);
            final var userDetails =  cache.get(userName, () -> userService.loadUserByUsername(userName));
            if (userDetails == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (jwtService.isTokenValid(jwt, userDetails)) {
                final var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (final SignatureException | MalformedJwtException e) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(final HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (final Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

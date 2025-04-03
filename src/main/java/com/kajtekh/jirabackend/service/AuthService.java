package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.auth.AuthenticationRequest;
import com.kajtekh.jirabackend.model.auth.AuthenticationResponse;
import com.kajtekh.jirabackend.model.auth.RegisterRequest;
import com.kajtekh.jirabackend.model.auth.TokenResponse;
import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.UserRepository;
import com.kajtekh.jirabackend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.kajtekh.jirabackend.model.user.Role.USER;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository repository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.repository = repository;
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
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = repository.findByUsernameOrEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public TokenResponse getTokenPayload(String token) {
        return jwtService.getTokenPayload(token);
    }
}

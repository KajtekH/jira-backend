package com.kajtekh.jirabackend.model.auth;

public record AuthenticationResponse(String accessToken, String refreshToken) {
}

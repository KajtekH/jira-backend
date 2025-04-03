package com.kajtekh.jirabackend.model.auth;

public record TokenResponse(String email, String username, String role, Integer exp) {
}

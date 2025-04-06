package com.kajtekh.jirabackend.model.auth;

public record TokenResponse(String email, String username, String firstName, String lastName, String role, Integer exp) {
}

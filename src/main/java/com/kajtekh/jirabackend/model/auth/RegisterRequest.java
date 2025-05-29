package com.kajtekh.jirabackend.model.auth;


public record RegisterRequest(String username, String password, String email, String firstName, String lastName) {
}

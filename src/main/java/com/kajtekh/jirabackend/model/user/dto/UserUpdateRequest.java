package com.kajtekh.jirabackend.model.user.dto;

public record UserUpdateRequest(String username, String email, String firstName, String lastName) {
}

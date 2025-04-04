package com.kajtekh.jirabackend.model.user.dto;

import com.kajtekh.jirabackend.model.user.User;

public record UserUpdateRequest(String username, String email, String firstName, String lastName) {
    public static UserUpdateRequest from(User user) {
        return new UserUpdateRequest(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}

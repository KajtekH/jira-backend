package com.kajtekh.jirabackend.model.user.dto;

import com.kajtekh.jirabackend.model.user.User;
import lombok.NonNull;

public record UserResponse(Long id, @NonNull String username, @NonNull String email, String firstName, String lastName, boolean isActive) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive()
        );
    }
}

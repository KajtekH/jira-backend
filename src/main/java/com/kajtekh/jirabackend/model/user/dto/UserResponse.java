package com.kajtekh.jirabackend.model.user.dto;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import lombok.NonNull;

import java.io.Serializable;

public record UserResponse(Long id, @NonNull String username, @NonNull String email, String firstName, String lastName, boolean isActive, Role role) implements Serializable {

    public static UserResponse from(final User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                user.getRole()
        );
    }
}

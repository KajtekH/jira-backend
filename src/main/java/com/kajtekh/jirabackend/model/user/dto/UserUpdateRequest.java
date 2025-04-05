package com.kajtekh.jirabackend.model.user.dto;

import com.kajtekh.jirabackend.model.user.User;

public record UserUpdateRequest(String username, String email, String firstName, String lastName) {
}

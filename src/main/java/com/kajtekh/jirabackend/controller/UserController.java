package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.dto.UserResponse;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream().map(UserResponse::from).toList());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable final Long id, @RequestBody final Role role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> activateUser(@PathVariable final Long id, @RequestBody final boolean active) {
        userService.changeActive(id, active);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable final Long id, @RequestBody final UserUpdateRequest userUpdateRequest) {
        final var user = userService.getUserById(id);
        final var updatedUser = userService.updateUser(user, userUpdateRequest);
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

}

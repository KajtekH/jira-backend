package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.facade.UserFacade;
import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.dto.UserResponse;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
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

    private final UserFacade userFacade;

    public UserController(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userFacade.getAllUsers());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable final Long id, @RequestBody final Role role) {
        userFacade.updateUserRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> activateUser(@PathVariable final Long id, @RequestBody final boolean active) {
        userFacade.activateUser(id, active);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable final Long id, @RequestBody final UserUpdateRequest userUpdateRequest) {
     final var updatedUser = userFacade.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

}

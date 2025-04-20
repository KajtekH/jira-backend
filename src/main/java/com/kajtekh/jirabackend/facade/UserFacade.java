package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.dto.UserResponse;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacade {

    private final UserService userService;
    private final UpdateNotificationService updateNotificationService;
    private final Cache cache;

    public UserFacade(final UserService userService, final UpdateNotificationService updateNotificationService, final Cache cache) {
        this.userService = userService;
        this.updateNotificationService = updateNotificationService;
        this.cache = cache;
    }

    @Cacheable(value = "data", key = "'users'")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    public void updateUserRole(final Long id, final Role role) {
        final var user = userService.updateUserRole(id, role);
        final var username = user.getUsername();
        cache.evictIfPresent("users");
        cache.put(username, UserResponse.from(user));
        updateNotificationService.notifyUserListUpdate();
    }

    public void activateUser(final Long id, final boolean active) {
        final var user = userService.changeActive(id, active);
        final var username = user.getUsername();
        cache.evictIfPresent("users");
        cache.put(username, UserResponse.from(user));
        updateNotificationService.notifyUserListUpdate();
    }

    public UserResponse updateUser(final Long id, final UserUpdateRequest userUpdateRequest) {
        final var user = userService.getUserById(id);
        final var updatedUser = userService.updateUser(user, userUpdateRequest);
        final var username = updatedUser.getUsername();
        cache.evictIfPresent("users");
        cache.put(username, UserResponse.from(updatedUser));
        updateNotificationService.notifyUserListUpdate();
        return UserResponse.from(updatedUser);
    }
}

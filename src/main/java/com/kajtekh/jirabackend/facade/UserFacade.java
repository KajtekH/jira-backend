package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.model.user.dto.UserResponse;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacade {
    private static final Logger LOG = LoggerFactory.getLogger(UserFacade.class);
    private static final String CACHE_EVICTED_MSG = "Cache evicted for key: users";
    private static final String USERS_CACHE_KEY = "users";
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
        LOG.debug("Fetching all users");
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    public void updateUserRole(final Long id, final Role role) {
        LOG.debug("Updating user with ID: '{}' to role: '{}'", id, role);
        final var user = userService.updateUserRole(id, role);
        final var username = user.getUsername();
        cache.evictIfPresent(USERS_CACHE_KEY);
        cache.put(username, UserResponse.from(user));
        LOG.trace(CACHE_EVICTED_MSG);
        updateNotificationService.notifyUserListUpdate();
    }

    public void changeActivation(final Long id, final boolean active) {
        LOG.debug("Changing activation for user with ID: '{}' to {}", id, active);
        final var user = userService.changeActivation(id, active);
        final var username = user.getUsername();
        cache.evictIfPresent(USERS_CACHE_KEY);
        cache.put(username, UserResponse.from(user));
        LOG.trace(CACHE_EVICTED_MSG);
        updateNotificationService.notifyUserListUpdate();
    }

    public UserResponse updateUser(final Long id, final UserUpdateRequest userUpdateRequest) {
        LOG.debug("Updating user with ID: '{}'", id);
        final var user = userService.getUserById(id);
        final var updatedUser = userService.updateUser(user, userUpdateRequest);
        final var username = updatedUser.getUsername();
        cache.evictIfPresent(USERS_CACHE_KEY);
        cache.put(username, UserResponse.from(updatedUser));
        LOG.trace(CACHE_EVICTED_MSG);
        updateNotificationService.notifyUserListUpdate();
        return UserResponse.from(updatedUser);
    }

    public List<String> getUsersByRole(final Role role) {
    LOG.debug("Fetching users with role: '{}'", role);
    return userService.getUsersByRole(role).stream()
                .map(User::getUsername)
                .toList();
    }
}

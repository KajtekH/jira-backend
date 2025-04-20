package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String USERNAME_NOT_FOUND = "User with username '{}' not found";
    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            LOG.warn(USERNAME_NOT_FOUND, username);
            throw new UsernameNotFoundException(USER_NOT_FOUND + username);
        });
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username).orElseGet(() -> {
            LOG.warn(USERNAME_NOT_FOUND, username);
            throw new UsernameNotFoundException(USER_NOT_FOUND + username);
        });
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(final String username) {
        return userRepository.findByUsernameOrEmail(username).orElseGet(() -> {
            LOG.warn(USERNAME_NOT_FOUND, username);
            throw new UsernameNotFoundException(USER_NOT_FOUND + username);
        });
    }

    @Transactional
    public User updateUserRole(final Long id, final Role role) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        user.setRole(role);
        userRepository.save(user);
        LOG.info("User role for user with ID '{}' updated to '{}'", id, role);
        return user;
    }

    @Transactional
    public User changeActive(final Long id, final boolean active) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        user.setActive(active);
        userRepository.save(user);
        LOG.info("User with ID '{}' activated", id);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(final Long id) {
        return userRepository.findById(id).orElseGet(() -> {
            LOG.warn("User with ID '{}' not found", id);
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        });
    }

    @Transactional
    public User updateUser(final User user, final UserUpdateRequest userUpdateRequest) {
        user.setUsername(userUpdateRequest.username());
        user.setEmail(userUpdateRequest.email());
        user.setFirstName(userUpdateRequest.firstName());
        user.setLastName(userUpdateRequest.lastName());
        userRepository.save(user);
        LOG.info("User with ID '{}' updated successfully", user.getId());
        return user;
    }

    @Transactional
    public void save(final User user) {
        userRepository.save(user);
    }
}

package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.exception.InsufficientRoleException;
import com.kajtekh.jirabackend.exception.UserNotFoundException;
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
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String USER_WITH_USERNAME_NOT_FOUND = "User with username '{}' not found";
    private static final String USER_WITH_ID_NOT_FOUND = "User with ID '{}' not found";
    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getWorker(final String username) {
        if (username == null || username.isBlank()) {
            LOG.warn("Username is null or blank");
            return Optional.empty();
        }
        final var user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        if(user.getRole() != Role.WORKER) {
            LOG.warn("User with username '{}' is not a worker", username);
            throw new InsufficientRoleException("User is not a worker");
        }
        return Optional.of(user);
    }

    public User getProductManager(final String username) {
        final var user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        if(user.getRole() != Role.PRODUCT_MANAGER) {
            LOG.warn("User with username '{}' is not a product manager", username);
            throw new InsufficientRoleException("User is not a product manager");
        }
        return user;
    }

    public User getAccountManager(final String username) {
        final var user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        if(user.getRole() != Role.ACCOUNT_MANAGER) {
            LOG.warn("User with username '{}' is not an account manager", username);
            throw new InsufficientRoleException("User is not an account manager");
        }
        return user;
    }

    public User getOwner(final String username) {
        final var user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        if(user.getRole() != Role.OWNER) {
            LOG.warn("User with username '{}' is not an owner", username);
            throw new InsufficientRoleException("User is not an owner");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(final String username) {
        return userRepository.findByUsernameOrEmail(username).orElseThrow(() -> {
            LOG.warn(USER_WITH_USERNAME_NOT_FOUND, username);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
    }

    @Transactional
    public User updateUserRole(final Long id, final Role role) {
        final User user = userRepository.findById(id).orElseThrow(() -> {
            LOG.warn(USER_WITH_ID_NOT_FOUND, id);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        user.setRole(role);
        userRepository.save(user);
        LOG.info("User role for user with ID '{}' updated to '{}'", id, role);
        return user;
    }

    @Transactional
    public User changeActivation(final Long id, final boolean active) {
        final User user = userRepository.findById(id).orElseThrow(() -> {
            LOG.warn(USER_WITH_ID_NOT_FOUND, id);
            return new UserNotFoundException(USER_NOT_FOUND);
        });
        user.setActive(active);
        userRepository.save(user);
        if (active) {
            LOG.info("User with ID '{}' activated", id);
        } else {
            LOG.info("User with ID '{}' deactivated", id);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(final Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            LOG.warn(USER_WITH_ID_NOT_FOUND, id);
            return new UserNotFoundException(USER_NOT_FOUND);
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

    public List<User> getUsersByRole(final Role role) {
        LOG.debug("Fetching users with role: '{}'", role);
        return userRepository.findByRole(role).orElseThrow(() -> {
            LOG.warn("No users found with role: '{}'", role);
            return new UserNotFoundException("No users found with the specified role");
        });
    }
}

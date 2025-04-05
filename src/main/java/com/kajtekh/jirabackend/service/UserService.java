package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.model.user.dto.UserUpdateRequest;
import com.kajtekh.jirabackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void updateUserRole(final Long id, final Role role) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public void changeActive(final Long id, final boolean active) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setActive(active);
        userRepository.save(user);
    }

    public User getUserById(final Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(final User user, final UserUpdateRequest userUpdateRequest) {
        user.setUsername(userUpdateRequest.username());
        user.setEmail(userUpdateRequest.email());
        user.setFirstName(userUpdateRequest.firstName());
        user.setLastName(userUpdateRequest.lastName());
        return userRepository.save(user);
    }
}

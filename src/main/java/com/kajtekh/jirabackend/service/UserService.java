package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.user.Role;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public void changeActive(Long id, boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setActive(active);
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

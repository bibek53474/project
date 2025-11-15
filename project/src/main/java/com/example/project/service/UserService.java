package com.example.project.service;

import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(String username, String email, String password, String roleName) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        // Set roles
        Set<Role> roles = new HashSet<>();
        Role.RoleName roleEnum;
        try {
            roleEnum = Role.RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleName);
        }
        
        // Get or create role with proper error handling for concurrent access
        Role role = roleRepository.findByName(roleEnum)
                .orElseGet(() -> {
                    try {
                        Role newRole = new Role();
                        newRole.setName(roleEnum);
                        return roleRepository.save(newRole);
                    } catch (DataIntegrityViolationException e) {
                        // Role might have been created by another thread/process
                        // Try to fetch it again
                        return roleRepository.findByName(roleEnum)
                                .orElseThrow(() -> new RuntimeException("Failed to create or find role: " + roleName));
                    }
                });
        roles.add(role);
        user.setRoles(roles);
        
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Handle case where user might have been created concurrently
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("Username is already taken!");
            }
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email is already in use!");
            }
            throw new RuntimeException("Failed to register user. Please try again.");
        }
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new RuntimeException("User not found: " + usernameOrEmail)));
    }
    
    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals(roleName));
    }
    
    public boolean hasAnyRole(User user, String... roleNames) {
        Set<String> roleNameSet = Set.of(roleNames);
        return user.getRoles().stream()
                .anyMatch(role -> roleNameSet.contains(role.getName().name()));
    }
}


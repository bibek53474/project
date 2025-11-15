package com.example.project.config;

import com.example.project.entity.Role;
import com.example.project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        try {
            initializeRoles();
        } catch (Exception e) {
            log.error("Error initializing roles: {}", e.getMessage(), e);
        }
    }
    
    private void initializeRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            try {
                roleRepository.findByName(roleName).orElseGet(() -> {
                    try {
                        Role role = new Role();
                        role.setName(roleName);
                        Role savedRole = roleRepository.save(role);
                        log.info("Created role: {}", roleName);
                        return savedRole;
                    } catch (DataIntegrityViolationException e) {
                        // Role might have been created by another thread/process
                        log.warn("Role {} already exists (possibly created concurrently)", roleName);
                        return roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Failed to create or find role: " + roleName));
                    }
                });
            } catch (Exception e) {
                log.error("Error initializing role {}: {}", roleName, e.getMessage());
                // Continue with other roles even if one fails
            }
        }
        log.info("Roles initialization completed");
    }
}


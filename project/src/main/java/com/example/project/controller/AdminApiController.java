package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.entity.User;
import com.example.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {
    
    private final UserService userService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> adminDashboardApi() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsernameOrEmail(username);
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome to Admin Dashboard");
        data.put("user", username);
        data.put("roles", user.getRoles());
        
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard accessed successfully", data));
    }
    
    @GetMapping("/users/{username}")
    public ResponseEntity<ApiResponse> getUserInfo(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("enabled", user.isEnabled());
            userData.put("roles", user.getRoles());
            
            return ResponseEntity.ok(ApiResponse.success("User information retrieved", userData));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


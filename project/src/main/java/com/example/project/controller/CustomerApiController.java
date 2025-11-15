package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
public class CustomerApiController {
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> customerDashboardApi() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome to Customer Dashboard");
        data.put("user", username);
        
        return ResponseEntity.ok(ApiResponse.success("Customer dashboard accessed successfully", data));
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("message", "Customer profile information");
        
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", data));
    }
}


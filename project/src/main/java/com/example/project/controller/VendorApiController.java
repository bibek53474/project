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
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class VendorApiController {
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> vendorDashboardApi() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome to Vendor Dashboard");
        data.put("user", username);
        
        return ResponseEntity.ok(ApiResponse.success("Vendor dashboard accessed successfully", data));
    }
    
    @GetMapping("/products")
    public ResponseEntity<ApiResponse> getProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Vendor products");
        data.put("user", username);
        data.put("products", "Product list here");
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", data));
    }
}


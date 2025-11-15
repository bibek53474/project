package com.example.project.controller;

import com.example.project.entity.User;
import com.example.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
    
    @GetMapping("/users/search")
    public String searchUser(@RequestParam(required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            try {
                User user = userService.findByUsername(username);
                model.addAttribute("user", user);
                model.addAttribute("found", true);
            } catch (RuntimeException e) {
                model.addAttribute("error", "User not found");
                model.addAttribute("found", false);
            }
        }
        return "admin-dashboard";
    }
}


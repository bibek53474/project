package com.example.project.controller;

import com.example.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FormAuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              @RequestParam String role,
                              RedirectAttributes redirectAttributes) {
        try {
            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }
            
            userService.registerUser(username, email, password, role);
            redirectAttributes.addAttribute("success", "true");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}


package com.example.project.controller;

import com.example.project.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FormPasswordResetController {
    
    private final PasswordResetService passwordResetService;
    
    @PostMapping("/reset-password/request")
    public String requestPasswordReset(@RequestParam String email,
                                      RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.requestPasswordReset(email);
            redirectAttributes.addAttribute("success", "true");
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "An error occurred. Please try again.");
        }
        return "redirect:/reset-password";
    }
    
    @PostMapping("/reset-password/confirm")
    public String confirmPasswordReset(@RequestParam String token,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Validate password confirmation
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addAttribute("error", "Passwords do not match");
                redirectAttributes.addAttribute("token", token);
                return "redirect:/reset-password/confirm";
            }
            
            passwordResetService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("success", "true");
            // Redirect back to confirm page to show success message
            return "redirect:/reset-password/confirm?token=" + token + "&success=true";
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password/confirm";
        }
    }
}


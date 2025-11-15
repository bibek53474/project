package com.example.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(@RequestParam(required = false) String error,
                          @RequestParam(required = false) String success,
                          Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", true);
        }
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String success,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", true);
        }
        return "reset-password";
    }
    
    @GetMapping("/reset-password/confirm")
    public String resetPasswordConfirm(@RequestParam(required = false) String token,
                                      @RequestParam(required = false) String error,
                                      @RequestParam(required = false) String success,
                                      Model model) {
        if (token != null) {
            model.addAttribute("token", token);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        // Always set success attribute to avoid null issues in template
        model.addAttribute("success", success != null && "true".equals(success));
        return "reset-password-confirm";
    }
    
    /**
     * Handle favicon requests to prevent 404 errors in logs
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


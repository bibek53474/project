package com.example.project.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Determine redirect URL based on user roles from authentication authorities
        String targetUrl = determineTargetUrl(authentication);
        
        // Clear any saved request and redirect
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Check if user has ADMIN role
        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return "/admin/dashboard";
        }
        
        // Check if user has VENDOR role
        boolean isVendor = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_VENDOR"));
        
        if (isVendor) {
            return "/vendor/dashboard";
        }
        
        // Default: redirect to landing page for customers and other users
        return "/";
    }
}


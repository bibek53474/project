// Main JavaScript utilities for User Authentication System

(function() {
    'use strict';

    // Initialize tooltips and popovers
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize Bootstrap tooltips if available
        if (typeof bootstrap !== 'undefined') {
            const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        }

        // Add fade-in animation to cards on load
        const cards = document.querySelectorAll('.card');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            setTimeout(() => {
                card.style.transition = 'all 0.5s ease-out';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });

        // Smooth scroll for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                const href = this.getAttribute('href');
                if (href !== '#' && href.length > 1) {
                    e.preventDefault();
                    const target = document.querySelector(href);
                    if (target) {
                        target.scrollIntoView({
                            behavior: 'smooth',
                            block: 'start'
                        });
                    }
                }
            });
        });
    });

    // Form validation utilities
    window.FormValidator = {
        emailRegex: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        
        validateEmail: function(email) {
            return this.emailRegex.test(email);
        },
        
        validatePassword: function(password) {
            return {
                length: password.length >= 6,
                upper: /[A-Z]/.test(password),
                lower: /[a-z]/.test(password),
                number: /[0-9]/.test(password),
                special: /[!@#$%^&*(),.?":{}|<>]/.test(password)
            };
        },
        
        getPasswordStrength: function(password) {
            const requirements = this.validatePassword(password);
            const metCount = Object.values(requirements).filter(v => v).length;
            
            if (metCount <= 2) return 'weak';
            if (metCount <= 3) return 'medium';
            return 'strong';
        }
    };

    // Toast notification system
    window.Toast = {
        show: function(message, type = 'info', duration = 3000) {
            const toast = document.createElement('div');
            toast.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
            toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);';
            toast.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            
            document.body.appendChild(toast);
            
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 300);
            }, duration);
        },
        
        success: function(message, duration) {
            this.show(message, 'success', duration);
        },
        
        error: function(message, duration) {
            this.show(message, 'danger', duration);
        },
        
        info: function(message, duration) {
            this.show(message, 'info', duration);
        },
        
        warning: function(message, duration) {
            this.show(message, 'warning', duration);
        }
    };

    // Loading button utility
    window.LoadingButton = {
        setLoading: function(button, loading = true) {
            if (!button) return;
            
            const spinner = button.querySelector('.spinner-border');
            const text = button.querySelector('.btn-text');
            
            if (loading) {
                button.disabled = true;
                if (spinner) spinner.classList.remove('d-none');
                if (text) text.style.opacity = '0.6';
            } else {
                button.disabled = false;
                if (spinner) spinner.classList.add('d-none');
                if (text) text.style.opacity = '1';
            }
        }
    };

    // Password visibility toggle
    window.PasswordToggle = {
        init: function(inputId, toggleId, iconId) {
            const input = document.getElementById(inputId);
            const toggle = document.getElementById(toggleId);
            const icon = document.getElementById(iconId);
            
            if (!input || !toggle || !icon) return;
            
            toggle.addEventListener('click', function() {
                const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
                input.setAttribute('type', type);
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });
        }
    };

    // Auto-dismiss alerts
    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert.classList.contains('show')) {
                    alert.classList.remove('show');
                    setTimeout(() => alert.remove(), 300);
                }
            }, 5000);
        });
    });

    // Form submission enhancement
    window.enhanceFormSubmission = function(formId, options = {}) {
        const form = document.getElementById(formId);
        if (!form) return;
        
        const submitBtn = form.querySelector('button[type="submit"]');
        const defaultOptions = {
            showLoading: true,
            validateBeforeSubmit: true,
            successCallback: null,
            errorCallback: null
        };
        
        const config = { ...defaultOptions, ...options };
        
        form.addEventListener('submit', function(e) {
            if (config.validateBeforeSubmit && !form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                form.classList.add('was-validated');
                return false;
            }
            
            if (config.showLoading && submitBtn) {
                LoadingButton.setLoading(submitBtn, true);
            }
        });
    };

    // Debounce utility
    window.debounce = function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    // Throttle utility
    window.throttle = function(func, limit) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    };

})();


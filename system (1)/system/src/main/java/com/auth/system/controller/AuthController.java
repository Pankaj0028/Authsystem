package com.auth.system.controller;

import com.auth.system.dto.*;
import com.auth.system.entity.User;
import com.auth.system.service.UserService;
import com.auth.system.service.OTPService;
import com.auth.system.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; // Import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    
    private final UserService userService;
    private final OTPService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    public AuthController(UserService userService, OTPService otpService, 
                         EmailService emailService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }
    
    @GetMapping("/")
    public String welcome(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            userService.findByEmail(email).ifPresent(user -> model.addAttribute("user", user));
        }
        return "welcome";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/api/signup")
    @ResponseBody
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest signupRequest) {
        if (userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists."));
        }
        
        userService.createUser(signupRequest.getEmail(), signupRequest.getPassword(), signupRequest.getName());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!"));
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            // 1. Authenticate the user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // 2. *** CRITICAL FIX ***: Set the Authentication object in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Set user email in the HttpSession for application-specific use (e.g., ProfileController)
            session.setAttribute("email", loginRequest.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Login successful"));
        } catch (Exception e) {
            // This catches AuthenticationException from authenticationManager.authenticate()
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid email or password."));
        }
    }
    
    @PostMapping("/api/forgot-password/send-otp")
    @ResponseBody
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody OTPRequest otpRequest) {
        if (!userService.existsByEmail(otpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found with this email."));
        }
        
        otpService.sendOTPEmail(otpRequest.getEmail());
        
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email"));
    }
    
    @PostMapping("/api/forgot-password/reset")
    @ResponseBody
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody OTPRequest otpRequest) {
        if (!otpService.verifyOTP(otpRequest.getEmail(), otpRequest.getOtp())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired OTP"));
        }
        
        // This is a dummy generated password. In a real app, you'd accept a new password from the user
        String newPassword = otpService.generateOTP(); 
        Optional<User> userOptional = userService.findByEmail(otpRequest.getEmail());
        if (userOptional.isPresent()) {
            userService.updatePassword(userOptional.get().getId(), newPassword);
            emailService.sendOTPEmail(otpRequest.getEmail(), 
                "Your new password is: " + newPassword);
            
            return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
        }
        
        return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
    }
    
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        // Clear the application session attribute
        session.invalidate();
        // Clear the Spring Security context
        SecurityContextHolder.clearContext(); 
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
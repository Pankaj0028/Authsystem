package com.auth.system.controller;

import com.auth.system.dto.ApiResponse;
import com.auth.system.dto.ProfileUpdateRequest;
import com.auth.system.entity.User;
import com.auth.system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    private final UserService userService;
    
    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }
    
    // Renders the profile page
    @GetMapping
    public String profilePage(HttpSession session, Model model) {
        // NOTE: The user authentication is now handled by Spring Security due to the AuthController fix.
        // This session attribute check is for finding the specific user details.
        String email = (String) session.getAttribute("email");
        if (email != null) {
            Optional<User> userOptional = userService.findByEmail(email);
            userOptional.ifPresent(user -> model.addAttribute("user", user));
        }
        return "profile";
    }
    
    // Handles the profile update POST request
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody ProfileUpdateRequest updateRequest,
                                                   HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Not logged in"));
        }
        
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userService.updateUserProfile(
                userOptional.get().getId(),
                updateRequest.getName(),
                updateRequest.getPhone(),
                updateRequest.getBio(),
                updateRequest.getPassword() // CORRECTED: Pass the new password
            );
            
            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success("Profile updated successfully!", user));
            }
        }
        
        return ResponseEntity.badRequest().body(ApiResponse.error("Update failed. User not found."));
    }
}
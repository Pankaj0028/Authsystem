package com.auth.system.service;

import com.auth.system.entity.User;
import com.auth.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // ... (createUser, findByEmail, findById, existsByEmail, save methods) ...
    
    /**
     * Updates the user's profile and optionally their password.
     */
    public User updateUserProfile(Long userId, String name, String phone, String bio, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Update non-password fields
            if (name != null) user.setName(name);
            if (phone != null) user.setPhone(phone);
            if (bio != null) user.setBio(bio);
            
            // Securely hash and update password only if provided and not empty
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword)); 
            }
            
            return userRepository.save(user);
        }
        return null;
    }
    
    // Existing methods for completeness:
    public User createUser(String email, String password, String name) {
        User user = new User(email, passwordEncoder.encode(password));
        user.setName(name);
        user.setEnabled(true);
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public void updatePassword(Long userId, String newPassword) { 
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
}
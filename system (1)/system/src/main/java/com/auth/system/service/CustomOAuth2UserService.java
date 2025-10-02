package com.auth.system.service;

import com.auth.system.entity.CustomOAuth2User;
import com.auth.system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomOAuth2UserService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Load the default OAuth2User from the provider
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. Extract provider details
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase();
        
        // Use a standard key for the unique ID and email
        String providerId = oauth2User.getName(); 
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email is required and not received from " + provider);
        }

        // 3. Find or register the user
        Optional<User> userOptional = userService.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // Case 1: User exists (Login or Link)
            user = userOptional.get();
            // Update provider details if they are null (linking local account)
            if (user.getProvider() == null || user.getProvider().isEmpty() || "LOCAL".equals(user.getProvider())) {
                 updateExistingUser(user, provider, providerId);
            }
        } else {
            // Case 2: User does not exist (Sign Up)
            user = registerNewUser(email, name, provider, providerId);
        }

        // 4. Return the custom OAuth2 user object
        // The user object itself will be stored in the SecurityContext
        return new CustomOAuth2User(user, oauth2User, registrationId);
    }

    private User registerNewUser(String email, String name, String provider, String providerId) {
        User user = new User();
        user.setEmail(email);
        // Fallback to email prefix if name is not available
        user.setName(name != null ? name : email.split("@")[0]); 
        user.setProvider(provider);
        user.setProviderId(providerId);
        // Hash a dummy password (required if your authentication setup requires a non-null, encoded password)
        user.setPassword(passwordEncoder.encode("OAUTH_DUMMY_" + LocalDateTime.now().hashCode())); 
        user.setEnabled(true);
        return userService.save(user); // Assuming you have a public User save(User user) method in UserService
    }

    private void updateExistingUser(User user, String provider, String providerId) {
        // Update the existing user with social provider details
        user.setProvider(provider);
        user.setProviderId(providerId);
        userService.save(user);
    }
}
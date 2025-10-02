package com.auth.system.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User extends User implements OAuth2User {

    private OAuth2User oauth2User;
    private String registrationId;

    // Constructor for new Social User (Sign Up)
    public CustomOAuth2User(User user, OAuth2User oauth2User, String registrationId) {
        // Copy fields from the newly created/updated User entity
        this.setId(user.getId());
        this.setEmail(user.getEmail());
        this.setName(user.getName());
        this.setProvider(user.getProvider());
        this.setProviderId(user.getProviderId());
        this.setEnabled(user.isEnabled());
        // Do NOT set password here to avoid exposing encoded password if fetched.
        
        this.oauth2User = oauth2User;
        this.registrationId = registrationId;
    }

    // This method is required by OAuth2User, but we use the User entity's fields
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Simple fixed role for all users
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_USER");
    }

    // This method returns the unique identifier for the provider (e.g., Google user ID)
    @Override
    public String getName() {
        return oauth2User.getName();
    }
    
    // You can add a method to retrieve the underlying User entity if needed
    public User getUser() {
        return this;
    }
}
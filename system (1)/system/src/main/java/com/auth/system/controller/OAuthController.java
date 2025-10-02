package com.auth.system.controller;

import com.auth.system.entity.User;
import com.auth.system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class OAuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/oauth2/success")
    public String oauth2Success(OAuth2AuthenticationToken authenticationToken, 
                               HttpSession session) {
        OAuth2User oauth2User = authenticationToken.getPrincipal();
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String provider = authenticationToken.getAuthorizedClientRegistrationId();
        
        if (email == null) {
            return "redirect:/login?error=oauth_email_missing";
        }
        
        Optional<User> userOptional = userService.findByEmail(email);
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Create new user for OAuth
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email.split("@")[0]);
            user.setEnabled(true);
            user.setProvider(provider.toUpperCase());
            user.setPassword("OAUTH_USER"); // Dummy password for OAuth users
            user = userService.save(user);
        }
        
        session.setAttribute("email", user.getEmail());
        return "redirect:/";
    }
}
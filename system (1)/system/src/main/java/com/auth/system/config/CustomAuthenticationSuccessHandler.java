package com.auth.system.config;

import com.auth.system.entity.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        HttpSession session = request.getSession();

        // Check if the authentication is from OAuth2
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            // Set the necessary session attribute for your controllers (like ProfileController)
            session.setAttribute("email", oauthUser.getEmail());
            
        } 
        // Note: For standard form login, the session is usually handled by other filters.

        // Redirect to the home page (as configured in your existing web security)
        response.sendRedirect("/");
    }
}
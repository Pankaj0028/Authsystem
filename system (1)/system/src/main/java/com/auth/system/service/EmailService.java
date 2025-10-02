package com.auth.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    // Inject the JavaMailSender bean configured via application.properties
    private final JavaMailSender mailSender;
    
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Sends the One-Time Password (OTP) to the user's email for password reset.
     * @param toEmail The recipient's email address.
     * @param otp The generated OTP code.
     */
    public void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + 
                       "\n\nThis OTP will expire in 5 minutes. Do not share it with anyone.");
        
        mailSender.send(message);
    }
    
    /**
     * NEW FUNCTIONALITY: Sends the newly generated password to the user's email 
     * after successful OTP verification.
     * @param toEmail The recipient's email address.
     * @param newPassword The newly generated password.
     */
    public void sendNewPasswordEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your New Password");
        message.setText("Your password has been successfully reset." +
                       "\n\nYour new temporary password is: " + newPassword +
                       "\n\nPlease login immediately and change your password for security.");
        
        mailSender.send(message);
    }
    
    // Keeping the welcome email, but now using the real mail sender
    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to Auth System");
        message.setText("Hello " + name + ",\n\nWelcome to our system! Your account has been successfully created.");
        
        mailSender.send(message);
    }
}

//package com.auth.system.service;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//    
//    // Mock implementation that logs instead of sending real emails
//    public void sendOTPEmail(String toEmail, String otp) {
//        System.out.println("=== MOCK EMAIL ===");
//        System.out.println("To: " + toEmail);
//        System.out.println("Subject: Password Reset OTP");
//        System.out.println("OTP: " + otp);
//        System.out.println("=== END MOCK EMAIL ===");
//    }
//    
//    public void sendWelcomeEmail(String toEmail, String name) {
//        System.out.println("=== MOCK WELCOME EMAIL ===");
//        System.out.println("To: " + toEmail);
//        System.out.println("Subject: Welcome to Auth System");
//        System.out.println("Hello " + name + ", welcome to our system!");
//        System.out.println("=== END MOCK EMAIL ===");
//    }
//}


//package com.auth.system.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//    
//    private final JavaMailSender mailSender;
//    
//    @Autowired
//    public EmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//    
//    public void sendOTPEmail(String toEmail, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Password Reset OTP");
//        message.setText("Your OTP for password reset is: " + otp + 
//                       "\nThis OTP will expire in 5 minutes.");
//        mailSender.send(message);
//    }
//    
//    public void sendWelcomeEmail(String toEmail, String name) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Welcome to Auth System");
//        message.setText("Hello " + name + ",\n\nWelcome to our authentication system!");
//        mailSender.send(message);
//    }
//}
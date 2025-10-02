package com.auth.system.service;

import com.auth.system.repository.OTPVerificationRepository;
import com.auth.system.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {
    
    private final OTPVerificationRepository otpRepository;
    private final EmailService emailService;
    
    @Autowired
    public OTPService(OTPVerificationRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    
    public String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    public void saveOTP(String email, String otp) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        OTPVerification otpVerification = 
            new OTPVerification(email, otp, expiryTime);
        otpRepository.save(otpVerification);
    }
    
    public boolean verifyOTP(String email, String otp) {
        Optional<OTPVerification> otpOptional = 
            otpRepository.findByEmailAndOtpAndUsedFalse(email, otp);
        if (otpOptional.isPresent()) {
            OTPVerification otpVerification = otpOptional.get();
            if (LocalDateTime.now().isBefore(otpVerification.getExpiryTime())) {
                otpRepository.markOtpAsUsed(email, otp);
                return true;
            }
        }
        return false;
    }
    
    public void sendOTPEmail(String email) {
        String otp = generateOTP();
        saveOTP(email, otp);
        emailService.sendOTPEmail(email, otp);
    }
    
    // FIXED: Added @Transactional annotation for the scheduled task
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void cleanupExpiredOTPs() {
        try {
            otpRepository.deleteExpiredOtps(LocalDateTime.now());
            System.out.println("OTP cleanup completed successfully");
        } catch (Exception e) {
            System.out.println("OTP cleanup error (normal during development): " + e.getMessage());
        }
    }
}
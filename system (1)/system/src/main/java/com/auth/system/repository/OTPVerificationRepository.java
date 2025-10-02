package com.auth.system.repository;

import com.auth.system.entity.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {
    Optional<OTPVerification> findByEmailAndOtpAndUsedFalse(String email, String otp);
    
    @Modifying
    @Transactional
    @Query("UPDATE OTPVerification o SET o.used = true WHERE o.email = :email AND o.otp = :otp")
    void markOtpAsUsed(@Param("email") String email, @Param("otp") String otp);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OTPVerification o WHERE o.expiryTime < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
}
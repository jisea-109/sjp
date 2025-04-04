package com.tinystop.sjp.Auth.Email;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long>{
    Optional<EmailVerificationEntity> findByEmail(String email);
    boolean existsByEmailAndVerifiedTrue(String email);
    void deleteByEmail(String email);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationEntity data WHERE data.expiryTime < :now") // 안쓰는 데이터 삭제
    void deleteExpiredEmails(@Param("now") LocalDateTime now); 
}

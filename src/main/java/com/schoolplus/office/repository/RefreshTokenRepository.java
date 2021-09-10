package com.schoolplus.office.repository;

import com.schoolplus.office.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT t FROM RefreshToken t WHERE t.token = :value AND t.expiryDateTime > CURRENT_TIMESTAMP")
    Optional<RefreshToken> findByToken(@Param("value") String token);

    boolean existsByToken(String token);

    void deleteByToken(String string);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDateTime < CURRENT_TIMESTAMP")
    void deleteAllExpiredSince();

}

package com.d109.reper.repository;

import com.d109.reper.domain.UserToken;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserToken> findByUserId(Long userId);  // 특정 유저의 토큰 조회
    List<UserToken> findByStoreId(Long storeId);  // 특정 가게의 모든 직원 토큰 조회

    // 다수의 사용자 토큰 조회
    List<UserToken> findByUserIdIn(List<Long> userIds);
}

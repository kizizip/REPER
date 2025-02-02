package com.d109.reper.repository;

import com.d109.reper.domain.StoreEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Long> {

    // 특정 유저가 특정 매장에서 근무 중인지 확인
    boolean existsByUser_UserIdAndStore_StoreIdAndIsEmployedTrue(Long userId, Long storeId);
}

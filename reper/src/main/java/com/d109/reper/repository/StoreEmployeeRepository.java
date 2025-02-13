package com.d109.reper.repository;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Long> {
    boolean existsByStoreAndUser(Store store, User user); //해당 조합 존재 확인
    Optional<StoreEmployee> findByStoreAndUser(Store store, User user);

    // 특정 유저가 특정 매장에서 근무 중인지 확인
    boolean existsByUser_UserIdAndStore_StoreIdAndIsEmployedTrue(Long userId, Long storeId);

    // 특정 알바생의 isEmployed = ture 인 storeEmployee의 storeEmployee들 조회
    List<StoreEmployee> findByUserAndIsEmployedTrue(User user);


    // 특정 매장에서 isEmployed=true 직원(User)만 조회
    @Query("SELECT se.user FROM StoreEmployee se WHERE se.store.storeId = :storeId AND se.isEmployed = true")
    List<User> findUsersByStoreIdAndIsEmployedTrue(@Param("storeId") Long storeId);


}


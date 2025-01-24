package com.d109.reper.repository;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Long> {
    boolean existsByStoreAndUser(Store store, User user); //해당 조합 존재 확인
    Optional<StoreEmployee> findByStoreAndUser(Store store, User user);

}
package com.d109.reper.repository;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreEmployeeRepository extends JpaRepository<StoreEmployee, Long> {

    // storeId + userId 조합으로 권한 요청이 이미 존재하는지 확인
    boolean requestAlreadyExists(Store store, User user);
}

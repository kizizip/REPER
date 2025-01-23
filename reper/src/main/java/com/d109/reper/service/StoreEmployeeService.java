package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import com.d109.reper.response.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreEmployeeService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;

    @Transactional
    public StoreEmployee addStoreEmployee(Long storeId, Long userId) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 storeId입니다."));

        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 userId입니다."));

        // 중복 체크
        boolean exists = storeEmployeeRepository.existsByStoreAndUser(store, user);
        if (exists) {
            throw new ConflictException("해당 가게/직원 조합으로 권한 요청이 이미 존재합니다.");
        }

        // StoreEmployee 생성 및 저장
        StoreEmployee storeEmployee = new StoreEmployee();
        storeEmployee.setStore(store);
        storeEmployee.setUser(user);
        storeEmployee.setEmployed(false); // 기본값 false 설정
        return storeEmployeeRepository.save(storeEmployee);
    }
}

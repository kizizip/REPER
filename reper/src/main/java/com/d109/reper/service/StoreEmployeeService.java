package com.d109.reper.service;

import com.d109.reper.controller.StoreEmployeeController;
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



    //시험용으로 만듭니다.
    @Transactional
    public StoreEmployee employeeInfo(Long storeId, Long userId) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 storeId입니다."));

        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 userId입니다."));

        StoreEmployee employee = storeEmployeeRepository.findByStoreAndUser(store, user)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        return employee;
    }

    // 알바생->사장 권한 요청 (store_employee 테이블에 추가)
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

        // StoreEmployee 생성, 저장
        StoreEmployee storeEmployee = new StoreEmployee();
        storeEmployee.setStore(store);
        storeEmployee.setUser(user);
        storeEmployee.setIsEmployed(false); // 기본값 false 설정
        return storeEmployeeRepository.save(storeEmployee);
    }


    // 사장 -> 알바생 권한 승인 (is_employed 값을 true로 변환)
    @Transactional
    public boolean updateIsEmployed(Long storeId, Long userId) {
        try {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("EmployeeNotFound"));

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("EmployeeNotFound"));


            StoreEmployee storeEmployee = storeEmployeeRepository.findByStoreAndUser(store, user)
                    .orElseThrow(() -> new IllegalArgumentException("EmployeeNotFound"));

            if (storeEmployee.isEmployed()) {
                throw new ConflictException("이미 승인된 직원입니다.");
            }

            storeEmployee.setIsEmployed(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 권한 요청 거절 or 알바생 삭제하기 (테이블에서 데이터 삭제)

}

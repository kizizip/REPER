package com.d109.reper.service;

import com.d109.reper.controller.StoreEmployeeController;
import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreEmployeeService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;


    // 알바생 정보를 조회하기 위한 메서드
    @Transactional
    public StoreEmployee employeeInfo(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        StoreEmployee employee = storeEmployeeRepository.findByStoreAndUser(store, user)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));

        return employee;
    }

    // 알바생->사장 권한 요청 (store_employee 테이블에 추가)
    @Transactional
    public StoreEmployee addStoreEmployee(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        // 중복 체크
        boolean exists = storeEmployeeRepository.existsByStoreAndUser(store, user);
        if (exists) {
            throw new IllegalStateException("해당 가게/직원 조합으로 권한 요청이 이미 존재합니다.");
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
                    .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

            StoreEmployee storeEmployee = storeEmployeeRepository.findByStoreAndUser(store, user)
                    .orElseThrow(() -> new IllegalArgumentException("EmployeeNotFound"));

            if (storeEmployee.isEmployed()) {
                throw new IllegalStateException("이미 승인된 직원입니다.");
            }

            storeEmployee.setIsEmployed(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 권한 요청 거절 or 알바생 삭제하기 (테이블에서 데이터 삭제)
    @Transactional
    public StoreEmployee deleteEmployee(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreNotFound"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        StoreEmployee storeEmployee = storeEmployeeRepository.findByStoreAndUser(store, user)
                .orElseThrow(() -> new IllegalArgumentException("EmployeeNotFound"));

        if (storeEmployee != null) {
            storeEmployeeRepository.delete(storeEmployee);
        }

        return storeEmployee;
    }


    // 특정 알바생이 근무하는 모든 매장 조회
        //is_employed = true 인 매장들만 조회
    public List<StoreEmployeeController.StaffStoresResponse> findStoresByEmployee(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        if (!user.getRole().equals(UserRole.STAFF)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 user는 STAFF가 아닙니다.");
        }

        List<StoreEmployee> storeEmployees = storeEmployeeRepository.findByUserAndIsEmployedTrue(user);

        return storeEmployees.stream()
                .map(se -> new StoreEmployeeController.StaffStoresResponse(se.getStore()))
                .collect(Collectors.toList());
    }

}

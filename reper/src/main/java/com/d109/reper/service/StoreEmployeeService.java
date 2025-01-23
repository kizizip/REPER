package com.d109.reper.service;

import com.d109.reper.domain.Store;
import com.d109.reper.domain.StoreEmployee;
import com.d109.reper.domain.User;
import com.d109.reper.repository.StoreEmployeeRepository;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.repository.UserRepository;
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
    public StoreEmployee requestPermission(Long storeId, Long userId) {
        // storeId와 userId가 유효한지 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("storeId 혹은 userId가 유효하지 않음."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("storeId 혹은 userId가 유효하지 않음."));

        // 해당 가게/직원 조합으로 권한 요청이 이미 존재하는지 확인
        if (storeEmployeeRepository.requestAlreadyExists(store, user)) {
//            throw new ConflictException("해당 가게/직원 조합으로 권한 요청이 이미 존재함.");
        }

        // 권한 요청을 위한 StoreEmployee 객체 생성
        StoreEmployee storeEmployee = new StoreEmployee();
        storeEmployee.setStore(store);
        storeEmployee.setUser(user);
        storeEmployee.setEmployed(false); // 기본적으로 false로 설정

        // StoreEmployee 엔티티 저장
        return storeEmployeeRepository.save(storeEmployee);
    }
}

package com.d109.reper.service;

import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isEmailDuplicate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("InvalidRequest: 파라미터가 전달되지 않음.");
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("InvalidFormat: 파라미터의 형식이 유효하지 않음.");
        }

        return userRepository.findByEmail(email).isPresent();
    }
}

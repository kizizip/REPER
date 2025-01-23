package com.d109.reper.service;

import com.d109.reper.controller.UserController;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.d109.reper.domain.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    // 회원가입
    public boolean join(UserController.JoinRequest joinRequest) {
        try {
            User user = new User(); //JoinRequest를 User엔티티로 변환함
            user.setEmail(joinRequest.getEmail());
            user.setPassword(joinRequest.getPassword());
            user.setUserName(joinRequest.getUserName());
            user.setPhone(joinRequest.getPhone());
            user.setRole(joinRequest.getRoleEnum());



            userRepository.save(user);
            logger.info("회원가입 성공 - userId: {}", user.getUserId());
            return true;
        } catch (Exception e) {
            logger.error("회원가입 실패 - 오류: {}", e.getMessage());
            return false;
        }
    }



    // 아이디 중복 확인
    public boolean isEmailDuplicate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("InvalidRequest: 파라미터가 전달되지 않음.");
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("InvalidFormat: 파라미터의 형식이 유효하지 않음.");
        }

        return userRepository.findByEmail(email).isPresent();
    }


    //로그인 유효성 검증
    public User validateLogin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("InvalidCredentials"));

        if (!user.getPassword().equals(password)) {
            logger.warn("비밀번호 불일치 - email: {}", email);
            throw new SecurityException("InvalidCredentials");
        }

        return user;
    }


    //사용자 로그인
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("InvalidCredentials"));

        if (!user.getPassword().equals(password)) {
            throw new SecurityException("InvalidCredentials");
        }

        // 로그인 성공 시 사용자 정보를 반환
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("email", user.getEmail());
        userInfo.put("username", user.getUserName());
        userInfo.put("role", user.getRole().name());
        userInfo.put("phone", user.getPhone());

        return userInfo;
    }


    //회원 정보 조회
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        // 사용자 정보를 Map으로 변환
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("userId", user.getUserId());
        userProfile.put("username", user.getUserName());
        userProfile.put("email", user.getEmail());
        userProfile.put("phone", user.getPhone());
        userProfile.put("role", user.getRole().name());
        userProfile.put("createdAt", user.getCreatedAt());

        return userProfile;
    }

}

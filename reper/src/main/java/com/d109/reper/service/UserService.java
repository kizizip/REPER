package com.d109.reper.service;

import com.d109.reper.controller.UserController;
import com.d109.reper.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.d109.reper.domain.User;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;



    // 회원가입
    public Long insertMember(User user) {
        // 패스워드 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // User를 저장하고, 저장된 User가 null이 아니면 성공으로 판단
        User savedUser = userRepository.save(user);
        logger.info("저장된 User: {}", savedUser);
        return savedUser != null ? savedUser.getUserId() : null;
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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("비밀번호 불일치 - email: {}", email);
            throw new SecurityException("InvalidCredentials");
        }

        return user;
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


    // 회원정보 수정
    public boolean updateUserInfo(Long userId, UserController.UserUpdateRequest updateRequest) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

            // 수정 가능한 필드만 업데이트 (이름과 전화번호)
            if (updateRequest.getUserName() != null) {
                user.setUserName(updateRequest.getUserName());
            }
            if (updateRequest.getPhone() != null) {
                user.setPhone(updateRequest.getPhone());
            }

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // 카카오 가입 여부 변경 (X->O)
    public boolean updateKakaoJoin(Long userId) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

            user.setKakao(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 구글 가입 여부 변경 (X->O)
    public boolean updateGoogleJoin(Long userId) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

            user.setGoogle(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // 이메일 기준으로 사용자 정보 찾기
    public User findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("UserNotFound"));
            return user;
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad Request");
        }
    }

    // 회원 탈퇴
    // 최초 실행시 기능이 동작하지 않아 GPT에게 물어보았는데, @Transactional을 붙이라는 답변을 받았습니다.
    // @Transactional을 명시하니 기능이 잘 동작합니다. 그러나 다른 메서드는 이를 명시하지 않아도 동작하여, 나중에 차이점을 다시 검색하려고 주석 덧붙입니다.
    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findByUserId(userId);

        if (user.isPresent()) {
            userRepository.deleteByUserId(userId);
            return true;
        } else {
            return false;
        }
    }


    // 카카오 가입 여부 판단
    public boolean findKakaoJoin(String email) {
        boolean result = userRepository.findKakaoByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        return result;
    }

    // 구글 가입 여부 판단
    public boolean findGoogleJoin(String email) {
        boolean result = userRepository.findGoogleByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        return result;
    }
}

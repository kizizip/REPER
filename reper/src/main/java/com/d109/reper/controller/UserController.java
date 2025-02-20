package com.d109.reper.controller;

import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.repository.UserRepository;
import com.d109.reper.response.KakaoLoginResponseDto;
import com.d109.reper.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.K;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.d109.reper.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    //회원가입
    @PostMapping
    @Operation(summary = "사용자 정보를 추가합니다. 성공하면 저장된 userId를 리턴합니다. ", description = "모든 정보를 입력해야 회원가입이 가능합니다.")
    public ResponseEntity<?> join(@RequestBody JoinRequest joinRequest) {
        log.info("insertMember::: {}", joinRequest);

        // 이메일 중복 확인
        if (userService.isEmailDuplicate(joinRequest.getEmail())) {
            throw new IllegalArgumentException("이메일이 이미 존재함.");
        }

        User user = new User();
        user.setEmail(joinRequest.getEmail());
        user.setPassword(joinRequest.getPassword());
        user.setUserName(joinRequest.getUserName());
        user.setPhone(joinRequest.getPhone());
        user.setRole(joinRequest.getRoleEnum());  // Enum 변환

        Long userId = userService.insertMember(user);

        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            throw new RuntimeException("회원가입 실패");
        }
    }


    // 이메일 중복 확인
    @GetMapping("/email/check-duplication")
    @Operation(summary = "중복 email이면 true를 반환합니다.")
    public ResponseEntity<Boolean> checkEmailDuplication(@RequestParam(value = "email") String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("파라미터가 전달되지 않았습니다.");
        }

        if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("파라미터의 형식이 유효하지 않습니다.");
        }

        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }


    // 사용자 로그인
    // 쿠키 이용 방식
    @PostMapping("/login")
    @Operation(summary = "로그인 처리 후 성공적으로 로그인 되었다면 쿠키(loginId)를 포함한 일부 정보를 내려보냅니다.",
            description = "<pre>email와 pass 두개만 넘겨도 정상동작한다. \n 아래는 id, pass만 입력한 샘플코드\n"
                    + "{\r\n" + "  \"email\": \"example@example.com\",\r\n" + "  \"pass\": \"aa12\"\r\n" + "}" + "</pre>")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
        logger.info("로그인 요청 - email: {}", loginRequest.getEmail());

        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("email 혹은 password 누락");
        }

        User user = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());

        // 쿠키 생성
        Cookie cookie = new Cookie("loginId", URLEncoder.encode(user.getEmail(), "UTF-8"));
        cookie.setHttpOnly(true);  // XSS 방지
        cookie.setSecure(false);  // HTTPS에서만 전송 (테스트 시 false, 실제 서비스에서는 true 권장)
        cookie.setPath("/");
//        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
        cookie.setMaxAge(60 * 15); //15분
        response.addCookie(cookie);

        logger.info("쿠키 - cookie: {} = {}", cookie.getName(), cookie.getValue());

        // Response Body 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", user.getUserId());
        responseBody.put("username", user.getUserName());
        responseBody.put("role", user.getRole().name());
        responseBody.put("loginIdCookie", cookie.getValue());

        return ResponseEntity.ok(responseBody);
    }


    // 회원 정보 조회
    @GetMapping("/{userId}/info")
    @Operation(summary = "userId를 기입하면 회원정보를 반환합니다.")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }


    // 회원정보 수정
    @PatchMapping("/{userId}")
    @Operation(summary = "사용자의 개인정보를 수정합니다.", description = "사용자는 이름과 전화번호를 수정할 수 있습니다.")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long userId, @RequestBody UserUpdateRequest updateRequest) {
        try {
            // 사용자 정보 수정
            boolean result = userService.updateUserInfo(userId, updateRequest);

            if (result) {
                return ResponseEntity.ok(updateRequest);
            } else {
                throw new NoSuchElementException("해당하는 사용자가 없음.");
            }
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }
    }


    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    @Operation(summary = "userId 입력시 회원 정보를 삭제합니다.")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("회원 삭제: {}", userId);
        boolean isDeleted = userService.deleteUser(userId);


        if (isDeleted) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "정상적으로 삭제됨.");
            responseBody.put("userId", userId);

            return ResponseEntity.ok(responseBody);
        } else {
            throw new NoSuchElementException("회원 정보를 찾을 수 없음.");
        }
    }


    // 카카오 가입 여부 변경 (X->O)
    @PatchMapping("/{userId}/kakao")
    @Operation(summary = "카카오 연동시 boolean=true로 변경합니다.")
    public ResponseEntity<?> updateKakaoJoin(@PathVariable Long userId) {
        try {
            boolean result = userService.updateKakaoJoin(userId);

            if (result) {
                return ResponseEntity.ok(true);
            } else {
                throw new NoSuchElementException("해당하는 사용자가 없음.");
            }
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }
    }

    // 구글 가입 여부 변경 (X->O)
    @PatchMapping("/{userId}/google")
    @Operation(summary = "구글계정 연동시 boolean=true로 변경합니다.")
    public ResponseEntity<?> updateGoogleJoin(@PathVariable Long userId) {
        try {
            boolean result = userService.updateGoogleJoin(userId);

            if (result) {
                return ResponseEntity.ok(true);
            } else {
                throw new NoSuchElementException("해당하는 사용자가 없음.");
            }
        } catch (Exception e) {
            throw new RuntimeException("서버 오류 발생");
        }
    }

    @PostMapping("/cookie/{email}")
    @Operation(summary = "구글 로그인 성공시 cookie를 내려보냅니다.")
    public ResponseEntity<?> googleCookie(@PathVariable String email, HttpServletResponse response) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 쿠키 생성
            Cookie cookie = new Cookie("loginId", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // HTTPS에서만 전송 (테스트 시 false, 실제 배포 환경에서는 true)
            cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
            cookie.setMaxAge(60 * 15); // 15분 동안 유지

            response.addCookie(cookie);

            logger.info("쿠키 설정: {} = {}", cookie.getName(), cookie.getValue());

            KakaoLoginResponseDto responseDto = new KakaoLoginResponseDto(user);


            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("쿠키 생성 실패: {}", e.getMessage());
            throw new RuntimeException("서버 오류 발생");
        }
    }


    @GetMapping("/{email}/kakao")
    @Operation(summary = "해당 계정으로 kakao 소셜 가입이 되어있는지를 boolean타입으로 반환합니다.")
    public boolean kakaoJoinOrNot(@PathVariable String email) {
        boolean result = userService.findKakaoJoin(email);
        return result;
    }

    @GetMapping("/{email}/google")
    @Operation(summary = "해당 계정으로 google 소셜 가입이 되어있는지를 boolean타입으로 반환합니다.")
    public boolean googleJoinOrNot(@PathVariable String email) {
        boolean result = userService.findGoogleJoin(email);
        return result;
    }


    // 로그인 api에서 예시 request를 보여주기 위한 형식적 DTO
    public static class LoginRequest {

        @Setter
        @Schema(description = "사용자의 이메일", example = "example@example.com", required = true)
        private String email;

        @Schema(description = "사용자의 비밀번호", example = "password123", required = true)
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }


    public static class UserUpdateRequest {

        @Setter
        @Schema(description = "사용자의 이름", example = "홍길동")
        private String userName;

        @Setter
        @Schema(description = "사용자의 전화번호", example = "010-9876-5432")
        private String phone;

        public String getUserName() {
            return userName;
        }

        public String getPhone() {
            return phone;
        }
    }


    public static class PasswordUpdateRequest {

        @Setter
        @Schema(description = "변경할 비밀번호", example = "newpassword")
        private String password;

        public String getPassword() { return password; }
    }


    //회원가입 api에서 예시 request를 보여주기 위한 DTO
    public static class JoinRequest {

        @Setter
        @Schema(description = "사용자의 이메일", example = "example@example.com", required = true)
        private String email;

        @Setter
        @Schema(description = "사용자의 비밀번호", example = "password123", required = true)
        private String password;

        @Setter
        @Schema(description = "사용자의 이름", example = "홍길동", required = true)
        private String userName;

        @Setter
        @Schema(description = "사용자의 전화번호", example = "010-1234-5678", required = true)
        private String phone;

        @Setter
        @Schema(description = "사용자의 권한 (OWNER 또는 STAFF)", example = "OWNER", required = true)
        private String role;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPhone() {
            return phone;
        }

        public String getRole() {
            return role;
        }

        // UserRole Enum으로 변환
        @JsonIgnore
        public UserRole getRoleEnum() {
            try {
                return UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return UserRole.STAFF; // 기본값 설정
            }
        }
    }
}

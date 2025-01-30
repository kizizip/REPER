package com.d109.reper.controller;

import com.d109.reper.jwt.JwtTokenProvider;
import com.d109.reper.service.UserService;
import com.d109.reper.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.d109.reper.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);



//    // 회원가입
//    @PostMapping
//    @Operation(summary = "사용자 정보를 추가합니다. 성공하면 true를 리턴합니다. ", description = "모든 정보를 입력해야 회원가입이 가능합니다.")
//    public ResponseEntity<?> signup(@RequestBody JoinRequest joinRequest) {
//
//            // 이메일 중복 확인
//            if (userService.isEmailDuplicate(joinRequest.getEmail())) {
//                throw new IllegalArgumentException("이메일이 이미 존재함.");
//            }
//
//            boolean result = userService.join(joinRequest);
//
//            if (result) {
//                return ResponseEntity.ok(true);
//            } else {
//                throw new RuntimeException("회원가입 실패");
//            }
//    }


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
    //쿠키 이용 방식
//    @PostMapping("/login")
//    @Operation(summary = "로그인 처리 후 성공적으로 로그인 되었다면 쿠키(loginId)를 포함한 일부 정보를 내려보냅니다.",
//            description = "<pre>email와 pass 두개만 넘겨도 정상동작한다. \n 아래는 id, pass만 입력한 샘플코드\n"
//                    + "{\r\n" + "  \"email\": \"example@example.com\",\r\n" + "  \"pass\": \"aa12\"\r\n" + "}" + "</pre>")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
//        logger.info("로그인 요청 - email: {}", loginRequest.getEmail());
//
//        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
//                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
//            throw new IllegalArgumentException("email 혹은 password 누락");
//        }
//
//        User user = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());
//
//        // 쿠키 생성
//        Cookie cookie = new Cookie("loginId", URLEncoder.encode(user.getEmail(), "UTF-8"));
//        cookie.setHttpOnly(true);  // XSS 방지
//        cookie.setSecure(false);  // HTTPS에서만 전송 (테스트 시 false, 실제 서비스에서는 true 권장)
//        cookie.setPath("/");
//        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
//        response.addCookie(cookie);
//
//        logger.info("쿠키 - cookie: {} = {}", cookie.getName(), cookie.getValue());
//
//        // Response Body 생성
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("userId", user.getUserId());
//        responseBody.put("username", user.getUserName());
//        responseBody.put("role", user.getRole().name());
//        responseBody.put("loginIdCookie", cookie.getValue());
//
//        return ResponseEntity.ok(responseBody);
//    }

    //로그인 (JWT 이용)
    @PostMapping("/login")
    @Operation(summary = "로그인 처리 후 JWT를 반환합니다.")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) throws Exception {
        logger.info("로그인 요청 - email: {}", loginRequest.getEmail());

        if(loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("email 혹은 password가 올바르지 않음.");
        }

        //사용자 인증
        User user = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());

        //JWT 생성 (여기서는 JwtTokenProvider 클래스를 사용한다.)

        String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        //Response Body 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", user.getUserId());
        responseBody.put("username", user.getUserName());
        responseBody.put("role", user.getRole().name());

        //JWT를 Authorization 헤더에 담아 응답
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);

        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
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


    // 비밀번호 변경




    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    @Operation(summary = "userId 입력시 회원 정보를 삭제합니다.")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.deleteUser(userId);


        if (isDeleted ) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "정상적으로 삭제됨.");
            responseBody.put("userId", userId);

            return ResponseEntity.ok(responseBody);
        } else {
            throw new NoSuchElementException("회원 정보를 찾을 수 없음.");
        }
    }



    //JWT 인가 기능 구현
    //올바른 JWT토큰을 가지고 있으면 HttpStatus.OK 메시지를 줌
    @GetMapping("/check")
    public ResponseEntity check() {
        return new ResponseEntity(HttpStatus.OK);
    }




    // 로그인 api에서 예시 request를 보여주기 위한 DTO
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
        public UserRole getRoleEnum() {
            return UserRole.valueOf(role.toUpperCase());
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

}

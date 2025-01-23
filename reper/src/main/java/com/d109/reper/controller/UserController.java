package com.d109.reper.controller;

import com.d109.reper.service.UserService;
import com.d109.reper.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.d109.reper.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.d109.reper.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 회원가입
    @PostMapping
    @Operation(summary = "사용자 정보를 추가합니다. 성공하면 true를 리턴합니다. ", description = "모든 정보를 입력해야 회원가입이 가능합니다.")
    public ResponseEntity<?> signup(@RequestBody JoinRequest joinRequest) {
        try {
            logger.debug("회원가입 요청 - user: {}", joinRequest);

            // 이메일 중복 확인
            if (userService.isEmailDuplicate(joinRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("이메일이 이미 존재합니다.");
            }

            boolean result = userService.join(joinRequest);

            if (result) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("회원가입 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생");
        }
    }


    // 이메일 중복 확인
    @GetMapping("/email/check-duplication")
    @Operation(summary = "중복 email이면 true를 반환합니다.")
    public ResponseEntity<?> checkEmailDuplication(@RequestParam(value = "email") String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("InvalidRequest: 파라미터가 전달되지 않음.");
            }

            if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new IllegalArgumentException("InvalidFormat: 파라미터의 형식이 유효하지 않음.");
            }

            boolean isDuplicate = userService.isEmailDuplicate(email);
            return ResponseEntity.ok(isDuplicate);
        } catch (IllegalArgumentException e) {
            String[] errorParts = e.getMessage().split(": ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(errorParts[0], errorParts[1]));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ServerError", "서버 오류 발생"));
        }
    }


    // 사용자 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인 처리 후 성공적으로 로그인 되었다면 쿠키(loginId)를 포함한 일부 정보를 내려보냅니다.", description = "<pre>email와 pass 두개만 넘겨도 정상동작한다. \n 아래는 id, pass만 입력한 샘플코드\n"
            + "{\r\n" + "  \"email\": \"example@example.com\",\r\n" + "  \"pass\": \"aa12\"\r\n" + "}" + "</pre>")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            logger.info("로그인 요청 - email: {}", loginRequest.getEmail());

            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new IllegalArgumentException("InvalidRequest: email 혹은 password 누락");
            }

            User user = userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());

            // 로그인 성공 시 쿠키 생성
            Cookie cookie = new Cookie("loginId", URLEncoder.encode(user.getEmail(), "UTF-8"));
            cookie.setHttpOnly(true); //XSS 방지
            cookie.setSecure(false); //HTTPS에서만 전송
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
            response.addCookie(cookie);

            //쿠키 이름(loginId), 쿠키 값(example@example.com) 로그에 출력
            logger.info("쿠키 - cookie: {} = {}", cookie.getName(), cookie.getValue());

            // 필요한 사용자 정보만 Response Body에 포함
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", user.getUserId());
            responseBody.put("username", user.getUserName());
            responseBody.put("role", user.getRole().name());
            responseBody.put("loginIdCookie", cookie.getValue());

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidRequest", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("InvalidCredentials", "email 혹은 password가 잘못됨"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ServerError", "서버 내부 오류 발생"));
        }
    }


    // 회원 정보 조회
    @GetMapping("/{userId}/info")
    @Operation(summary = "userId를 기입하면 회원정보를 반환합니다.")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        try {
            Map<String, Object> userInfo = userService.getUserInfo(userId);
            return ResponseEntity.ok(userInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("UserNotFound", "사용자 ID가 유효하지 않음."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("ServerError", "서버 내부 에러 발생"));
        }
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
}

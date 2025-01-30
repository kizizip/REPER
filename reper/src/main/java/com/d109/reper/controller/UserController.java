package com.d109.reper.controller;

import com.d109.reper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.*;
import com.d109.reper.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인
    @GetMapping("/email/check-duplication")
    @Operation(summary = "중복 email이면 true를 반환합니다.")
    public ResponseEntity<?> checkEmailDuplication(@RequestParam(value = "email", required = false) String email) {
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
}

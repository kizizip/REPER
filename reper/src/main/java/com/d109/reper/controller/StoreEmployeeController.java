package com.d109.reper.controller;

import com.d109.reper.response.ConflictException;
import com.d109.reper.service.StoreEmployeeService;
import com.d109.reper.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import com.d109.reper.domain.StoreEmployee;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreEmployeeController {

    private final StoreEmployeeService storeEmployeeService;
    private static final Logger logger = LoggerFactory.getLogger(StoreEmployeeService.class);

    // 알바생->사장 권한 요청 (데이터 추가)
    @PostMapping("/{storeId}/employees/{userId}/approve")
    @Operation(summary = "알바생->사장 권한 요청", description = "알바생 정보를 가게에 등록합니다. isEmployed는 기본값 false로 설정됩니다.")
    public ResponseEntity<?> requestStorePermission(
            @PathVariable Long storeId,
            @PathVariable Long userId) {

        try {
            StoreEmployee storeEmployee = storeEmployeeService.addStoreEmployee(storeId, userId);

            // 응답에서 isEmployed 값을 true로 설정
            ResponseBody response = new ResponseBody(
                    "정상적으로 요청되었습니다.",
                    storeEmployee.getStore().getStoreId(),
                    storeEmployee.getUser().getUserId(),
                    false
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("storeId 혹은 userId가 유효하지 않음.")
            );
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse("해당 가게/직원 조합으로 권한 요청이 이미 존재함.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse("DB 처리 중 서버 오류 발생")
            );
        }
    }


    // 사장->알바생 권한 승인 (is_employed 값을 true로 변환)
    @PatchMapping("/{storeId}/employees/{userId}/approve")
    @Operation(summary = "사장->알바생 권한 승인", description = "storeId와 userId를 넘기면 is_employed값이 true로 바뀝니다. 성공시 true를 반환합니다.")
    public ResponseEntity<Object> approveEmployee(@PathVariable Long storeId, @PathVariable Long userId) {
        try {
            boolean already = storeEmployeeService.employeeInfo(storeId, userId).isEmployed();
            logger.info("already값", already);

            if (already) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("이미 승인된 직원임."));
            }

            boolean isUpdated = storeEmployeeService.updateIsEmployed(storeId, userId);

            return ResponseEntity.ok(true);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("storeId 혹은 userId가 유효하지 않음.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("DB 처리 중 서버 오류 발생"));
        }
    }


    // 권한 요청 거정 or 알바생 자르기 (알바생 정보 삭제)
    @DeleteMapping("/{storeId}/employees/{userId}")
    @Operation(summary = "권한 요청 거절 or 알바생 자르기", description = "store_employee 테이블에서 알바생 정보를 삭제합니다.")
    public ResponseEntity<Object> deleteEmployee(@PathVariable Long storeId, @PathVariable Long userId) {
        try {
            StoreEmployee storeEmployee = storeEmployeeService.deleteEmployee(storeId, userId);

            // 삭제 성공시 응답
            return ResponseEntity.ok(new ResponseBody(
                    "정상적으로 삭제되었습니다.",
                    storeId,
                    userId,
                    storeEmployee.isEmployed()
            ));
        } catch (Exception e) {
            // 예외 발생 시 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("DB 처리 중 서버 오류 발생"));
        }
    }



    // 성공 응답 형식
    public static class ResponseBody {
        private String message;
        private Long storeId;
        private Long userId;
        private boolean isEmployed;

        public ResponseBody(String message, Long storeId, Long userId, boolean isEmployed) {
            this.message = message;
            this.storeId = storeId;
            this.userId = userId;
            this.isEmployed = isEmployed;
        }

        // Getters
        public String getMessage() {
            return message;
        }

        public Long getStoreId() {
            return storeId;
        }

        public Long getUserId() {
            return userId;
        }

        public boolean getIsEmployed() {
            return isEmployed;
        }
    }

    // 에러 응답 형식
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}

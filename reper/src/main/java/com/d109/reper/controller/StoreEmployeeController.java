package com.d109.reper.controller;

import com.d109.reper.response.ConflictException;
import com.d109.reper.service.StoreEmployeeService;
import lombok.RequiredArgsConstructor;
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

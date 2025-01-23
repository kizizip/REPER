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

    // 알바생 -> 사장 가게 등록 권한 요청
    @PostMapping("/{storeId}/employees/{userId}/approve")
    @Operation(summary = "알바생이 가게 등록 권한을 요청합니다.",
            description = "권한 요청시 store_employee 테이블에 추가됩니다.")
    public ResponseEntity<?> requestStorePermission(
            @PathVariable Long storeId,
            @PathVariable Long userId) {

        try {
            StoreEmployee storeEmployee = storeEmployeeService.requestPermission(storeId, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(storeEmployee);
        } catch (IllegalArgumentException e) {
            // storeId 혹은 userId가 유효하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("storeId 혹은 userId가 유효하지 않음.")
            );
        } catch (ConflictException e) {
            // 이미 권한 요청이 존재함
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse("해당 가게/직원 조합으로 권한 요청이 이미 존재함.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse("DB 처리 중 서버 오류 발생")
            );
        }
    }

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

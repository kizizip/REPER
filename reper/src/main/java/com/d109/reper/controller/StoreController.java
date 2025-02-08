package com.d109.reper.controller;

import com.d109.reper.domain.Store;
import com.d109.reper.elasticsearch.StoreDocument;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.request.StoreRequestDto;
import com.d109.reper.response.StoreResponseDto;
import com.d109.reper.response.UserResponseDto;
import com.d109.reper.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 매장 정보 등록
    @PostMapping
    @Operation(summary = "매장 정보 등록")
    public ResponseEntity<StoreResponseDto> createStore(@RequestBody StoreRequestDto storeRequestDto) {
        StoreResponseDto response = storeService.createStore(storeRequestDto);
        return ResponseEntity.ok(response);
    }


    // 매장 정보 조회(단건)
    @GetMapping("/{storeId}")
    @Operation(summary = "매장 정보 조회(단건)")
    public ResponseEntity<StoreResponseDto> getStore(@PathVariable Long storeId) {
        StoreResponseDto response = storeService.getStore(storeId);
        return ResponseEntity.ok(response);
    }


    // 매장 정보 삭제
    @DeleteMapping("/{storeId}")
    @Operation(summary = "매장 정보 삭제")
    public ResponseEntity<String> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.ok().body("Success delete store");
    }


    // 특정 매장의 전체 알바생 정보 조회
    @GetMapping("/{storeId}/employees")
    @Operation(summary = "특정 매장의 전체 알바생 정보 조회")
    public ResponseEntity<List<UserResponseDto>> getStoreEmployees(@PathVariable Long storeId) {
        List<UserResponseDto> reponse = storeService.getStoreEmployees(storeId);

        return ResponseEntity.ok(reponse);
    }


    // 사장님이 가진 모든 매장 조회
    @GetMapping("/owner/{userId}")
    @Operation(summary = "OWNER인 {userId}에 해당하는 모든 store를 조회합니다.")
    public ResponseEntity<List<StoreResponseDto>> findNotices(@PathVariable Long userId) {
        List<StoreResponseDto> response = storeService.findOwnerStores(userId);

        return ResponseEntity.ok(response);
    }



    // Elasticsearch 매장 제목 검색 기능
    @GetMapping("/search")
    @Operation(summary = "모든 매장 검색")
    public List<StoreDocument> searchStores(
            @RequestParam("storeName") String keyword) {
        return storeService.searchStoreByName(keyword);
    }


    // ResponseBody
    @Getter
    public static class StoreResponseAll {
        private final Long storeId;
        private final String storeName;

        public StoreResponseAll (Long storeId, String storeName) {
           this.storeId = storeId;
           this.storeName = storeName;
        }
    }

}

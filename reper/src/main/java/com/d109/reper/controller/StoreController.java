package com.d109.reper.controller;

import com.d109.reper.domain.Store;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.request.StoreRequestDto;
import com.d109.reper.response.StoreResponseDto;
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
        Store store = storeService.createStore(storeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StoreResponseDto(store));
    }

    // 매장 검색

    // 매장 정보 조회(단건)

    // 매장 정보 수정

    // 매장 정보 삭제

    // 사장님이 가진 모든 매장 조회
    @GetMapping("/owner/{userId}")
    @Operation(summary = "OWNER인 {userId}에 해당하는 모든 store를 조회합니다.")
    public ResponseEntity<List<StoreResponseAll>> findNotices(
            @PathVariable Long userId) {

        List<Store> stores = storeService.findStores(userId);

        List<StoreResponseAll> response = stores.stream()
                .map(store -> new StoreResponseAll(store.getStoreId(), store.getStoreName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
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

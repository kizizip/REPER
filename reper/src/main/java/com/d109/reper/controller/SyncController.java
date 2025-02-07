package com.d109.reper.controller;

import com.d109.reper.service.NoticeService;
import com.d109.reper.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// Elasticsearch 검색 기능 test. 더미 데이터 동기화용 API
@RestController
@RequestMapping("/sync")
public class SyncController {

    private final NoticeService noticeService;
    private final StoreService storeService;

    public SyncController(NoticeService noticeService, StoreService storeService) {
        this.noticeService = noticeService;
        this.storeService = storeService;
    }


    // 공지 제목 검색
    @PostMapping("/notices")
    @Operation(summary = "공지 검색 기능 더미 데이터 테스트용 API")
    public ResponseEntity<String> syncNoticesToElasticsearch() {
        noticeService.syncNoticesToElasticsearch();
        return ResponseEntity.ok("DB data has been successfully synchronized with Elasticsearch.");
    }

    // 매장 데이터 동기화 API
    @PostMapping("/stores")
    @Operation(summary = "매장 검색 기능 더미 데이터 동기화 API")
    public ResponseEntity<String> syncStoresToElasticsearch() {
        storeService.syncStoresToElasticsearch();
        return ResponseEntity.ok("Stores have been successfully synchronized with Elasticsearch.");
    }

}

package com.d109.reper.controller;

import com.d109.reper.service.NoticeService;
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

    public SyncController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    // 공지 제목 검색
    @PostMapping()
    @Operation(summary = "공지 검색 기능 더미 데이터 테스트용 API")
    public ResponseEntity<String> syncNoticesToElasticsearch() {
        noticeService.syncNoticesToElasticsearch();
        return ResponseEntity.ok("DB data has been successfully synchronized with Elasticsearch.");
    }

}

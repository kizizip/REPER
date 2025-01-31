package com.d109.reper.controller;

import com.d109.reper.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stores/{storeId}/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @Operation(summary = "{storeId}에 해당하는 공지를 생성합니다.")
    public ResponseEntity<Void> createNotice(
            @PathVariable Long storeId,
            @RequestBody Map<String, Object> requestBody) {

        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String title = requestBody.get("title").toString();
        String content = requestBody.get("content").toString();

        noticeService.saveNotice(userId, storeId, title, content);

        return ResponseEntity.ok("공지가 잘 등록되었습니다.");
    }
}

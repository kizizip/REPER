package com.d109.reper.controller;

import com.d109.reper.domain.Notice;
import com.d109.reper.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
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
    public ResponseEntity<ResponseBody> createNotice(
            @PathVariable Long storeId,
            @RequestBody Map<String, Object> requestBody) {

        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String title = requestBody.get("title").toString();
        String content = requestBody.get("content").toString();

        noticeService.saveNotice(userId, storeId, title, content);

        return ResponseEntity.ok(new ResponseBody(
                "공지가 정상적으로 등록되었습니다.",
                userId,
                storeId,
                title,
                content
        ));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지 하나를 조회합니다.")
    public ResponseEntity<ResponseBodyOne> findOneNotice(
            @PathVariable Long noticeId) {

        Notice notice = noticeService.findOneNotice(noticeId);

        return ResponseEntity.ok(new ResponseBodyOne(
                notice.getTitle(),
                notice.getContent()));
    }


    //성공 응답 형식
    @Getter
    public static class ResponseBody {
        private String message;
        private Long userId;
        private Long storeId;
        private String title;
        private String content;

        public ResponseBody(String message, Long userId, Long storeId, String title, String content) {
            this.message = message;
            this.userId = userId;
            this.storeId = storeId;
            this.title = title;
            this.content = content;
        }
    }

    // 조회 하나 응답
    @Getter
    public static class ResponseBodyOne {
        private String title;
        private String content;

        public ResponseBodyOne(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

}

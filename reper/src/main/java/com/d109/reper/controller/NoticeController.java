package com.d109.reper.controller;

import com.d109.reper.domain.Notice;
import com.d109.reper.service.NoticeService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
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

        Notice notice = noticeService.saveNotice(storeId, userId, title, content);

        return ResponseEntity.ok(new ResponseBody(
                "공지가 정상적으로 등록되었습니다.",
                notice.getNoticeId(),
                storeId,
                title,
                content,
                notice.getUpdatedAt()));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지 하나를 조회합니다.")
    public ResponseEntity<ResponseBodyOne> findOneNotice(
            @PathVariable Long noticeId,
            @PathVariable Long storeId,
            @RequestParam Long userId) {

        Notice notice = noticeService.findOneNotice(noticeId, storeId, userId);

        return ResponseEntity.ok(new ResponseBodyOne(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent()));
    }

    @GetMapping
    @Operation(summary = "{storeId}에 해당하는 전체 공지를 조회합니다.")
    public ResponseEntity<List<ResponseBodyAll>> findNotices(
            @PathVariable Long storeId,
            @RequestParam Long userId) {
        List<Notice> notices = noticeService.findNotices(storeId, userId);

        List<ResponseBodyAll> response = List.of(new ResponseBodyAll(
                storeId,
                notices.stream()
                        .map(notice -> new ResponseBodyAll.NoticeResponse(
                                notice.getNoticeId(), notice.getTitle(), notice.getContent()))
                        .toList()
        ));

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지를 수정합니다.")
    public ResponseEntity<NoticeController.ResponseBody> updateNotice(
            @PathVariable Long storeId,
            @PathVariable Long noticeId,
            @RequestBody Map<String, Object> requestBody) {

        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");

        NoticeController.ResponseBody responseBody = noticeService.updateNotice(noticeId, storeId, userId, title, content);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지를 삭제합니다.")
    public ResponseEntity<String> deleteNotice(
            @PathVariable Long noticeId,
            @PathVariable Long storeId,
            @RequestBody Map<String, Object> requestBody) {
        Long userId = Long.valueOf(requestBody.get("userId").toString());

        noticeService.deleteNotice(noticeId, storeId, userId);

        return ResponseEntity.ok("공지 삭제 완료");
    }

    // Response DTO
        //성공 응답 형식
    @Getter
    public static class ResponseBody {
        private String message;
        private Long noticeId;
        private Long storeId;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;

        public ResponseBody(String message, Long noticeId, Long storeId, String title, String content, LocalDateTime updatedAt) {
            this.message = message;
            this.noticeId = noticeId;
            this.storeId = storeId;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;

        }
    }

    // 조회 하나 응답
    @Getter
    public static class ResponseBodyOne {
        private Long noticeId;
        private String title;
        private String content;

        public ResponseBodyOne(Long noticeId, String title, String content) {
            this.noticeId = noticeId;
            this.title = title;
            this.content = content;
        }
    }

    // 매장별 전체 조회 응답
    @Getter
    public static class ResponseBodyAll {
        private Long storeId;
        private List<NoticeResponse> notices;

        public ResponseBodyAll(Long storeId, List<NoticeResponse> notices) {
            this.storeId = storeId;
            this.notices = notices;
        }

        @Getter
        public static class NoticeResponse {
            private Long noticeId;
            private String title;
            private String content;

            public NoticeResponse(Long noticeId, String title, String content) {
                this.noticeId = noticeId;
                this.title = title;
                this.content = content;
            }
        }
    };

}

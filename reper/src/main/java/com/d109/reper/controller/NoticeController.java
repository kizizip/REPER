package com.d109.reper.controller;

import com.d109.reper.domain.Notice;
import com.d109.reper.elasticsearch.NoticeDocument;
import com.d109.reper.service.NoticeService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
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
    public ResponseEntity<ResponseNoticeSave> createNotice(
            @PathVariable Long storeId,
            @RequestBody Map<String, Object> requestBody) {

        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String title = requestBody.get("title").toString();
        String content = requestBody.get("content").toString();

        Notice notice = noticeService.saveNotice(storeId, userId, title, content);

        return ResponseEntity.ok(new ResponseNoticeSave(
                "공지가 정상적으로 등록되었습니다.",
                notice.getNoticeId(),
                storeId,
                title,
                content,
                notice.getUpdatedAt()));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지 하나를 조회합니다.")
    public ResponseEntity<ResponseNoticeOne> findOneNotice(
            @PathVariable Long noticeId,
            @PathVariable Long storeId,
            @RequestParam Long userId) {

        Notice notice = noticeService.findOneNotice(noticeId, storeId, userId);

        return ResponseEntity.ok(new ResponseNoticeOne(
                notice.getNoticeId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getUpdatedAt()));
    }

    @GetMapping
    @Operation(summary = "{storeId}에 해당하는 전체 공지를 조회합니다.")
    public ResponseEntity<List<ResponseNotices>> findNotices(
            @PathVariable Long storeId,
            @RequestParam Long userId) {

        List<ResponseNotices> response = noticeService.findNotices(storeId, userId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지를 수정합니다.")
    public ResponseEntity<NoticeController.ResponseNoticeSave> updateNotice(
            @PathVariable Long storeId,
            @PathVariable Long noticeId,
            @RequestBody Map<String, Object> requestBody) {

        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");

        NoticeController.ResponseNoticeSave responseBody = noticeService.updateNotice(noticeId, storeId, userId, title, content);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "{noticeId}에 해당하는 공지를 삭제합니다.")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId,
            @PathVariable Long storeId,
            @RequestBody Map<String, Object> requestBody) {
        Long userId = Long.valueOf(requestBody.get("userId").toString());

        noticeService.deleteNotice(noticeId, storeId, userId);

        System.out.println("공지 삭제 완료");

        return ResponseEntity.noContent().build();
    }


    // 공지 제목 검색
    @GetMapping("/search/title")
    @Operation(summary = "공지 제목 검색")
    public List<NoticeDocument> searchNoticesTitle(
            @PathVariable Long storeId,
            @RequestParam("titleKeyword") String keyword) {
        return noticeService.searchNoticesTitle(storeId,keyword);
    }

    // 공지 내용 검색
    @GetMapping("/search/content")
    @Operation(summary = "공지 내용 검색")
    public List<NoticeDocument> searchNoticesContent(
            @PathVariable Long storeId,
            @RequestParam("contentKeyword") String keyword) {
        return noticeService.searchNoticesContent(storeId,keyword);
    }


    // Response DTO
        //성공 응답 형식
    @Getter
    public static class ResponseNoticeSave {
        private String message;
        private Long noticeId;
        private Long storeId;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd' 'HH:mm")
        private LocalDateTime updatedAt;

        public ResponseNoticeSave(String message, Long noticeId, Long storeId, String title, String content, LocalDateTime updatedAt) {
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
    public static class ResponseNoticeOne {
        private Long noticeId;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd' 'HH:mm")
        private LocalDateTime updatedAt;

        public ResponseNoticeOne(Long noticeId, String title, String content, LocalDateTime updatedAt) {
            this.noticeId = noticeId;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
        }
    }

    // 매장별 전체 조회 응답
    @Getter
    public static class ResponseNotices {
        private Long storeId;
        private List<NoticeResponse> notices;

        public ResponseNotices(Long storeId, List<NoticeResponse> notices) {
            this.storeId = storeId;
            this.notices = notices;
        }

        @Getter
        public static class NoticeResponse {
            private Long noticeId;
            private String title;
            private String content;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
            private LocalDateTime updatedAt;
            private String timeAgo;

            public NoticeResponse(Long noticeId, String title, String content, LocalDateTime updatedAt, String timeAgo) {
                this.noticeId = noticeId;
                this.title = title;
                this.content = content;
                this.updatedAt = updatedAt;
                this.timeAgo = timeAgo;
            }
        }
    };

}

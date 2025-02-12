package com.d109.reper.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class FcmMessageResponseDto {

    private boolean validateOnly;
    private Message message;

    @Getter
    @Setter
    @Builder
    public static class Message {
        private String token;
        private Notification notification;
        private Map<String, String> data; // ← 추가된 부분
    }

    @Getter
    @Setter
    @Builder
    public static class Notification {
        private String title;
        private String body;
    }
}

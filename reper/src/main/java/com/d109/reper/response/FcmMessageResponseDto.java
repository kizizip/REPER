package com.d109.reper.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
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

        // order FCM respose data 위해서 필드 추가
            // data 필요없는 알림들 위해 null 방지 default 설정
        @Builder.Default
        private Map<String, String> data = new HashMap<>();

    }

    @Getter
    @Setter
    @Builder
    public static class Notification {
        private String title;
        private String body;
    }
}

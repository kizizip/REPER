package com.d109.reper.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class FcmTopicMessageRequestDto {

    private boolean validateOnly;
    private Message message;

    @Getter
    @Setter
    @Builder
    public static class Message {
        private String topic;  // 🔥 기존 `token`과 분리된 토픽 필드
        private Notification notification;
    }

    @Getter
    @Setter
    @Builder
    public static class Notification {
        private String title;
        private String body;
    }
}

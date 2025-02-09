package com.d109.reper.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class FcmMessageRequest {
    private String token;  // FCM 대상 토큰
    private String title;  // 알림 제목
    private String body;   // 알림 내용

    public FcmMessageRequest(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }
}

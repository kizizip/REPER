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
    private String targetFragment;  // 추가된 필드: 프래그먼트 정보
    private Integer requestId;  // 추가된 필드: 요청 ID

    // 기본 생성자
    public FcmMessageRequest(String token, String title, String body, String targetFragment, Integer requestId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.targetFragment = targetFragment;
        this.requestId = requestId;
    }
}

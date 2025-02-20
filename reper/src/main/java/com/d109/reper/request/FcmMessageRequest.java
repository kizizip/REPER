package com.d109.reper.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FcmMessageRequest {
    private String token;  // FCM 대상 토큰
    private String title;  // 알림 제목
    private String body;   // 알림 내용
    private String targetFragment;  // 추가된 필드: 프래그먼트 정보
    private Integer requestId;  // 추가된 필드: 요청 ID
    private Map<String, String> data; // 추가된 필드: 알림에 추가 데이터 전달

    // 기본 생성자
    public FcmMessageRequest(String token, String title, String body, String targetFragment, Integer requestId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.targetFragment = targetFragment;
        this.requestId = requestId;

        // Map 초기화 및 데이터 추가
        this.data = new HashMap<>();  // data 초기화
        this.data.put("targetFragment", targetFragment);  // String으로 변환해서 넣기
        this.data.put("requestId", String.valueOf(requestId));  // Integer를 String으로 변환해서 넣기
    }

}

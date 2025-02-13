package com.d109.reper.controller;

import com.d109.reper.request.FcmMessageRequest;
import com.d109.reper.service.FcmMessageService;
import com.d109.reper.service.UserTokenService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmMessageController {

    private final FcmMessageService fcmMessageService;
    private final UserTokenService userTokenService; // 추가된 부분 ✅

    /**
     * 여러 사용자에게 FCM 메시지 전송
     * @param storeId 스토어 아이디
     * @param title 메시지 제목
     * @param body 메시지 본문
     * @param targetFragment 프래그먼트 정보
     * @param requestId 요청 ID
     */
    @PostMapping("/sendToStore/{storeId}")
    public void sendToStore(@PathVariable Long storeId,
                            @RequestParam String title,
                            @RequestParam String body,
                            @RequestParam String targetFragment,  // targetFragment 추가
                            @RequestParam Integer requestId) {  // requestId 추가
        // ✅ storeId에 해당하는 직원들의 FCM 토큰을 가져옴
        List<String> tokens = userTokenService.getTokensForStore(storeId);

        if (tokens != null && !tokens.isEmpty()) {
            // 여러 명에게 메시지를 보내는 요청을 FcmMessageRequest 객체로 변환하여 전달
            List<FcmMessageRequest> requests = tokens.stream()
                    .map(token -> new FcmMessageRequest(token, title, body, targetFragment, requestId))  // 추가된 파라미터 포함
                    .collect(Collectors.toList());
            fcmMessageService.sendToMultipleUsers(requests);
        } else {
            log.error("해당 storeId에 해당하는 직원들의 토큰을 찾을 수 없습니다: {}", storeId);
        }
    }

    /**
     * 한 명의 사용자에게 FCM 메시지 전송
     * @param userId 사용자 아이디
     * @param title 메시지 제목
     * @param body 메시지 본문
     * @param targetFragment 프래그먼트 정보
     * @param requestId 요청 ID
     */
    @PostMapping("/sendToUser/{userId}")
    public void sendToUser(@PathVariable Long userId,
                           @RequestParam String title,
                           @RequestParam String body,
                           @RequestParam String targetFragment,  // targetFragment 추가
                           @RequestParam Integer requestId) {  // requestId 추가
        // ✅ userId에 해당하는 사용자의 FCM 토큰을 가져옴
        String token = userTokenService.getTokenForUser(userId);

        if (token != null) {
            FcmMessageRequest messageRequest = new FcmMessageRequest(token, title, body, targetFragment, requestId);  // 추가된 파라미터 포함
            fcmMessageService.sendFcmMessage(messageRequest);
        } else {
            log.error("해당 userId에 해당하는 사용자 토큰을 찾을 수 없습니다: {}", userId);
        }
    }

    //JY 토픽 구독 추가
    @PostMapping("/subscribe")
    public String subscribeToTopic(@RequestBody FcmSubscribeRequest request) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                    Arrays.asList(request.getToken()), request.getTopic()
            );
            return "구독 성공: " + response.getSuccessCount();
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "구독 실패: " + e.getMessage();
        }
    }

    // 토픽 구독 테스트용 API1
        // 테스트용 토큰을 직접 지정하고 store_1 토픽에 구독하는 API
    @PostMapping("/test-subscribe")
    public ResponseEntity<String> testSubscribe() {
        String dummyToken = "test-dummy-token-123"; // 실제 기기가 없어도 설정 가능
        String topic = "store_50";

        try {
            // 명시적으로 FirebaseApp 지정
            FirebaseMessaging messaging = FirebaseMessaging.getInstance(FirebaseApp.getInstance());
            TopicManagementResponse response = messaging.subscribeToTopic(
                    Arrays.asList(dummyToken), topic
            );
            return ResponseEntity.ok("테스트 구독 성공: " + response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구독 실패: " + e.getMessage());
        }
    }

    // 테스트용 FCM 알림 전송 API
    @PostMapping("/test-fcm")
    public ResponseEntity<String> testFcm() {
        fcmMessageService.sendToTopic("store_50", "테스트 알림", "테스트 메시지입니다.");
        return ResponseEntity.ok("테스트 FCM 전송 완료!");
    }


}



class FcmSubscribeRequest {
    private String token;
    private String topic;

    // Getter, Setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}


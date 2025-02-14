package com.d109.reper.controller;

import com.d109.reper.request.FcmMessageRequest;
import com.d109.reper.service.FcmMessageService;
import com.d109.reper.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

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


}




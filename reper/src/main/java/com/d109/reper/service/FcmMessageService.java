package com.d109.reper.service;

import com.d109.reper.request.FcmMessageRequest;
import com.d109.reper.request.FcmTopicMessageRequestDto;
import com.d109.reper.response.FcmMessageResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmMessageService {

    private final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/reper-7e5b4/messages:send";
    private final String SERVICE_ACCOUNT_JSON_PATH = "src/main/resources/firebase/reper-7e5b4-firebase-adminsdk-fbsvc-ee6581830a.json";  // Firebase 서비스 계정 키 파일 경로

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String oauth2AccessToken;

    // 서비스 계정으로부터 액세스 토큰을 얻어오기 위한 초기화 작업
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(SERVICE_ACCOUNT_JSON_PATH);
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
            AccessToken token = credentials.refreshAccessToken();
            oauth2AccessToken = token.getTokenValue();
            log.info("액세스 토큰이 성공적으로 얻어졌습니다.");
        } catch (Exception e) {
            log.error("액세스 토큰 초기화 실패: ", e);
        }
    }

    /**
     * 여러 사용자에게 FCM 메시지 보내기
     */
    public void sendToMultipleUsers(List<FcmMessageRequest> requests) {
        initialize();
        try {
            for (FcmMessageRequest request : requests) {
                FcmMessageResponseDto messageDto = createFcmMessage(request);
                sendFcmNotification(messageDto);
            }
        } catch (Exception e) {
            log.error("FCM 다중 메시지 전송 실패: ", e);
        }
    }

    /**
     * 한 명에게 FCM 메시지 보내기
     */
    public void sendFcmMessage(FcmMessageRequest request) {
        initialize();
        FcmMessageResponseDto messageDto = createFcmMessage(request);
        sendFcmNotification(messageDto);
    }

    /**
     * FcmMessageRequest 객체를 이용하여 FCM 메시지 DTO 생성
     */
    private FcmMessageResponseDto createFcmMessage(FcmMessageRequest request) {
        return FcmMessageResponseDto.builder()
                .message(FcmMessageResponseDto.Message.builder()
                        .token(request.getToken())  // 한 개의 토큰만 전달
                        .notification(FcmMessageResponseDto.Notification.builder()
                                .title(request.getTitle())
                                .body(request.getBody())
                                .build())
                        .build())
                .build();
    }

    /**
     * 실제로 FCM 메시지를 전송하는 메서드
     */
    private void sendFcmNotification(FcmMessageResponseDto messageDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + oauth2AccessToken);  // 동적으로 얻은 액세스 토큰 사용

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(messageDto), headers);

            ResponseEntity<String> response = restTemplate.exchange(FCM_API_URL, HttpMethod.POST, request, String.class);

            log.info("FCM 메시지 응답: {}", response.getBody());
            log.info("FCM 메시지 응답 상태 코드: {}", response.getStatusCode());

        }catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: 상태 코드 - {}, 응답 본문 - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("FCM 메시지 전송 중 오류 발생: ", e);
        }
    }

    /**
     * FCM 토픽 기반 메시지를 전송하는 메서드
     */
    private void sendFcmNotificationForTopic(FcmTopicMessageRequestDto messageDto) {
        try {
            log.info("sendFcmNotificationForTopic 실행 됨");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + oauth2AccessToken);  // 동적으로 얻은 액세스 토큰 사용

            String jsonRequest = objectMapper.writeValueAsString(messageDto);
            log.info("📢 FCM 요청 본문: {}", jsonRequest); // ✅ FCM 요청 확인

            // JSON 변환 후 요청 본문 생성
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(messageDto), headers);

            // FCM API 호출 (토픽 메시지 전송)
            ResponseEntity<String> response = restTemplate.exchange(FCM_API_URL, HttpMethod.POST, request, String.class);

            log.info("FCM 토픽 메시지 응답: {}", response.getBody());
            log.info("FCM 토픽 메시지 응답 상태 코드: {}", response.getStatusCode());

        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: 상태 코드 - {}, 응답 본문 - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("FCM 토픽 메시지 전송 중 오류 발생: ", e);
        }
    }


    // orderFCM 토픽 기반으로 FCM 메시지 보내기
    public void sendToTopic(String topic, String title, String body) {
        initialize();

        FcmTopicMessageRequestDto messageDto = FcmTopicMessageRequestDto.builder()
                .message(FcmTopicMessageRequestDto.Message.builder()
                        .topic(topic)  // 토픽 기반 전송
                        .notification(FcmTopicMessageRequestDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .build())
                        .build())
                .build();
        try {
            sendFcmNotificationForTopic(messageDto);
            System.out.println("FCM 메세지 전송 성공: " + topic + " - " + title);
        } catch (Exception e) {
            System.out.println("FCM 메세지 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

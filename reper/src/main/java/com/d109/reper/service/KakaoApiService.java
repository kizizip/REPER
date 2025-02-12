package com.d109.reper.service;

import com.d109.reper.domain.KakaoUserInfo;
import com.d109.reper.response.KakaoUserInfoResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoApiService {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";

    public static String getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        //HTTP 요청 보내기
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(KAKAO_API_URL, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return "Failed to get user info: "+ response.getStatusCode();
        }
    }


    public static KakaoUserInfo getKakaoUserInfo2(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 보내기
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    KAKAO_API_URL, HttpMethod.GET, entity, KakaoUserInfoResponse.class);

            KakaoUserInfoResponse responseBody = response.getBody();
            if (responseBody != null) {
                KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
                kakaoUserInfo.setEmail(responseBody.getKakao_account().getEmail());
                kakaoUserInfo.setNickname(responseBody.getProperties().getNickname());
                return kakaoUserInfo;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Kakao user info: " + e.getMessage(), e);
        }

        return null;
    }

}

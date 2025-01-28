package com.d109.reper.controller;

import com.d109.reper.domain.KakaoUserInfo;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class KakaoLoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
        //1. 카카오 access token으로 카카오 사용자 정보 조회

        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoLoginRequest.getAccessToken());

        //2. db에서 해당 사용자 검색
        User user = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    // 3. 사용자 정보가 없으면, 새로 추가
                    User newUser = new User();
                    newUser.setEmail(kakaoUserInfo.getEmail());
                    newUser.setUserName(kakaoUserInfo.getNickname());
                    newUser.setPassword(""); //비밀번호는 kakao에서 관리하므로, 빈 값으로 설정
                    newUser.setRole(UserRole.STAFF); //기본 권한 설정
                    return userRepository.save(newUser);
                });

        //4. 사용자 정보 반환
        return ResponseEntity.ok(user);

    }

    @GetMapping("/kakao")
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";

        var headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        var entity = new org.springframework.http.HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    KakaoUserInfo.class
            );

            // 응답 확인을 위한 로그 추가
            System.out.println("Kakao API Response: " + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            // 예외 처리 및 디버깅 메시지 추가
            throw new RuntimeException("Failed to fetch Kakao user info: " + e.getMessage(), e);
        }
    }


    public static class KakaoLoginRequest {
        private String accessToken;

        // Getter와 Setter
        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

}



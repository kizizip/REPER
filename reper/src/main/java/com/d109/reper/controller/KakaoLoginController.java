package com.d109.reper.controller;

import com.d109.reper.domain.KakaoUserInfo;
import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.repository.UserRepository;
import com.d109.reper.response.KakaoLoginResponseDto;
import com.d109.reper.response.KakaoUserInfoResponse;
import com.d109.reper.service.KakaoApiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.Cookie;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class KakaoLoginController {

    @Autowired
    private UserRepository userRepository;


    //리디렉션 콜백 처리
    @GetMapping("/callback")
    @Operation(summary = "카카오 로그인 후 리디렉션 되는 콜백을 처리합니다.")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        // 카카오에서 전달받은 인증 코드를 이용해 액세스 토큰을 발급받음
        String accessToken = getKakaoAccessToken(code);
        log.info("카카오 액세스 토큰: " + accessToken);

        // 토큰을 통해 사용자 정보를 가져옴
        KakaoUserInfo kakaoUserInfo = KakaoApiService.getKakaoUserInfo2(accessToken);
        String userInfo = KakaoApiService.getKakaoUserInfo(accessToken);
        log.info("카카오 사용자 정보: " + kakaoUserInfo);
        log.info("카카오 사용자정보2: " + userInfo);

        // DB에서 사용자 검색 또는 새로 생성
        User user = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(kakaoUserInfo.getEmail());
                    newUser.setUserName(kakaoUserInfo.getNickname());
                    newUser.setPassword(""); // 비밀번호는 카카오에서 관리하므로 빈 값
                    newUser.setRole(UserRole.STAFF); // 기본 권한 설정
                    return userRepository.save(newUser);
                });

        // 사용자 정보 반환
        return ResponseEntity.ok(user);
    }

    // 인증 코드로 액세스 토큰 요청
    private String getKakaoAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        // 요청 파라미터 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "11a435ceceba6f2724e9f7b1b9b69f5b"); // 카카오 REST API 키
        body.add("redirect_uri", "http://localhost:8080/api/auth/callback");
        body.add("code", code);

        // POST 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // 응답에서 액세스 토큰 추출
        Map<String, String> responseBody = response.getBody();
        return responseBody != null ? responseBody.get("access_token") : null;
    }

    @PostMapping("/kakao")
    @Operation(summary = "accessToken을 날리면 User 테이블상의 모든 정보를 불러옵니다."
            , description = "GET메서드와 유사하나, User 엔티티를 기본형으로 합니다.")
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

        //3. 쿠키 생성
        // 쿠키 생성
        Cookie cookie = new Cookie("loginId", URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS에서만 전송 (테스트 시 false, 실제 배포 환경에서는 true)
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        cookie.setMaxAge(60 * 15); // 15분 동안 유지
        //4. 사용자 정보 반환
        return ResponseEntity.ok(new KakaoLoginResponseDto(user));

    }

    // 토큰으로 사용자 정보(일부) 가져오기
    @GetMapping("/kakao")
    @Operation(summary = "accessToken을 날리면 사용자의 email, nickname을 불러옵니다."
            , description = "POST 메서드와 유사하나, 카카오가 제공하는 정보를 불러옵니다.")
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더 설정
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        var entity = new HttpEntity<>(headers);

        try {
            // 카카오 API 호출
                    ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                            url,
                            org.springframework.http.HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class // 응답을 KakaoUserInfoResponse로 받음
            );

            // 응답에서 사용자 정보를 추출하여 KakaoUserInfo 객체에 담기
            KakaoUserInfoResponse responseBody = response.getBody();
            if (responseBody != null) {
                KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
                // KakaoUserInfoResponse에서 이메일과 닉네임을 가져와서 KakaoUserInfo에 세팅
                kakaoUserInfo.setEmail(responseBody.getKakao_account().getEmail());
                kakaoUserInfo.setNickname(responseBody.getProperties().getNickname());
                return kakaoUserInfo;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Kakao user info: " + e.getMessage(), e);
        }

        return null;
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



package com.d109.reper.controller;

import com.d109.reper.service.KakaoApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao")
@Api(tags = "Kakao API")
public class KakaoTestController {

    private final KakaoApiService kakaoApiService;

    @Autowired
    public KakaoTestController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @ApiOperation(value = "카카오 사용자 정보 가져오기", notes = "카카오에서 사용자 정보를 가져옵니다.")
    @GetMapping("/user-info")
    public String getKakaoUserInfo(@RequestHeader("Authorization") String accessToken) {
        // accessToken은 Authorization 헤더로 전달
        return kakaoApiService.getKakaoUserInfo(accessToken);
    }
}

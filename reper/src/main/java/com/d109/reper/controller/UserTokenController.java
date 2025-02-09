package com.d109.reper.controller;

import com.d109.reper.request.UserTokenDto;
import com.d109.reper.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class UserTokenController {

    private final UserTokenService userTokenService;

    /**
     * 클라이언트로부터 전달받은 유저 토큰 저장 또는 업데이트
     * @param userTokenDto 유저 토큰 DTO
     */
    @PostMapping("/save")
    public String saveUserToken(@RequestBody UserTokenDto userTokenDto) {
        userTokenService.saveUserToken(userTokenDto);
        return "토큰 저장 또는 갱신 완료";
    }
}

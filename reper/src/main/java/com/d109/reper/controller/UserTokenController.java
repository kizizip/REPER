package com.d109.reper.controller;

import com.d109.reper.request.UserTokenDto;
import com.d109.reper.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * storeId에 해당하는 직원들의 FCM 토큰을 삭제하는 API
     */
    @DeleteMapping("/deleteTokensForStore/{storeId}")
    public ResponseEntity<String> deleteTokensForStore(@PathVariable long storeId) {
        userTokenService.deleteTokensForStore(storeId);
        return ResponseEntity.ok("All tokens for store " + storeId + " have been deleted.");
    }

    /**
     * userId에 해당하는 사용자의 FCM 토큰을 삭제하는 API
     */
    @DeleteMapping("/deleteTokenForUser/{userId}")
    public ResponseEntity<String> deleteTokenForUser(@PathVariable long userId) {
        userTokenService.deleteTokenForUser(userId);
        return ResponseEntity.ok("Token for user " + userId + " has been deleted.");
    }
}

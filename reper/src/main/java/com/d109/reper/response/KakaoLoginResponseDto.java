package com.d109.reper.response;

import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class KakaoLoginResponseDto {
    private Long userId;
    private String email;
    private String userName;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;

    public KakaoLoginResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}

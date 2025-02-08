package com.d109.reper.response;

import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {
    private Long userId;
    private String userName;
    private String email;
    private String phone;
    private UserRole role;


    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
    }
}

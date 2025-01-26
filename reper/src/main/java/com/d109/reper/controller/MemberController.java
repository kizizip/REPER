package com.d109.reper.controller;

import com.d109.reper.domain.User;
import com.d109.reper.domain.UserRole;
import com.d109.reper.service.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MemberController {

    private final UserService userService;

    public MemberController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/member/register")
    public String register() {
        return "member/register";
    }

    @PostMapping("/member/successLogin")
    public String successLogin() {
        return "member/successLogin";
    }

    @PostMapping("/member/failureLogin")
    public String failureLogin() {
        return "error/loginError";
    }

    //회원가입
    @PostMapping("/member/insertMember")
    public String insertMember(@RequestBody JoinRequest joinRequest) {
        log.info("insertMember::: {}", joinRequest);

        User user = new User();
        user.setEmail(joinRequest.getEmail());
        user.setPassword(joinRequest.getPassword());
        user.setUserName(joinRequest.getUserName());
        user.setPhone(joinRequest.getPhone());
        user.setRole(joinRequest.getRoleEnum());  // Enum 변환

        int res = userService.insertMember(user);
        log.info("res ::: 0", res);
        if (res ==1) {
            return "redirect:/index";
        } else {
            return "redirect:/error/registerError";
        }
    }





    //회원가입 api에서 예시 request를 보여주기 위한 DTO
    public static class JoinRequest {

        @Setter
        @Schema(description = "사용자의 이메일", example = "example@example.com", required = true)
        private String email;

        @Setter
        @Schema(description = "사용자의 비밀번호", example = "password123", required = true)
        private String password;

        @Setter
        @Schema(description = "사용자의 이름", example = "홍길동", required = true)
        private String userName;

        @Setter
        @Schema(description = "사용자의 전화번호", example = "010-1234-5678", required = true)
        private String phone;

        @Setter
        @Schema(description = "사용자의 권한 (OWNER 또는 STAFF)", example = "OWNER", required = true)
        private String role;

        public String getEmail() {
            return email;
        }
        public String getPassword() {
            return password;
        }
        public String getUserName() {
            return userName;
        }
        public String getPhone() {
            return phone;
        }
        public String getRole() {
            return role;
        }

        // UserRole Enum으로 변환
        public UserRole getRoleEnum() {
            return UserRole.valueOf(role.toUpperCase());
        }
    }


}

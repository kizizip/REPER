//package com.d109.reper.controller;
//
//import com.d109.reper.config.KakaoProperties;
//import lombok.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/login")
//public class KakaoLoginPageController {
//
//
//    private final KakaoProperties kakaoProperties;
//
//    public KakaoLoginPageController(KakaoProperties kakaoProperties) {
//        this.kakaoProperties = kakaoProperties;
//    }
//
//    @GetMapping("/page")
//    public String loginPage(Model model) {
////        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
////                +kakaoProperties.getClientId()
////                +"&redirect_uri="
////                +kakaoProperties.getRedirectUri();
////        model.addAttribute("location", location);
////
////        return "login";
//
//        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
//                +kakaoProperties.getClientId()
//                +"&redirect_uri="
//                +kakaoProperties.getRedirectUri();
//    }
//}

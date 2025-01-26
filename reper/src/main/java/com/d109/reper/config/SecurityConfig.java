package com.d109.reper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests((authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/member/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 관련 경로 허용
                        .requestMatchers("/", "/**").permitAll()));

        http
                .formLogin(login -> login
                        .loginPage("/")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/member/successLogin")
                        .failureForwardUrl("/member/failureLogin")
                        .permitAll());

        return http.build();
    }

    //패스워드 암호화로 사용할 bean
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

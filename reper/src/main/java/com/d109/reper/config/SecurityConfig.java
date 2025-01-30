package com.d109.reper.config;

import com.d109.reper.jwt.JwtAuthenticationFilter;
import com.d109.reper.jwt.JwtAuthorizationFilter;
import com.d109.reper.jwt.PrincipalDetails;
import com.d109.reper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;


    //1/30
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // AuthenticationManager 생성
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(email -> new PrincipalDetails(
                        userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                ))
                .passwordEncoder(getPasswordEncoder()); // 암호화 방식 지정

        return authenticationManagerBuilder.build();
    }




    //HTTP 보안 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 커스텀 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(http)), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(http), userRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login").permitAll()  // 로그인 API는 모두 허용
                        .requestMatchers("/member/insertMember").permitAll()  // 회원가입 API는 모두 허용 (변경된 URI)
                        .requestMatchers("/api/users/check").hasAnyRole("USER")  // 인증된 사용자만 접근
//                        .requestMatchers("/api/user/manage/information").hasAnyRole("USER")  // 인증된 사용자만 접근
                        .requestMatchers("/api/users/email/check-duplication").permitAll()  // 이메일 중복 확인
                        .requestMatchers("/api/users/{userId}/info").hasAnyRole("USER")  // 회원 정보 조회
                        .requestMatchers("/api/users/{userId}").hasAnyRole("USER")  // 회원 정보 수정, 탈퇴
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 관련 경로 허용
                        .anyRequest().permitAll()  // 나머지 요청은 모두 허용
                );
        return http.getOrBuild();
    }


    //1/30 전 코딩
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrfConfig -> csrfConfig.disable())
//                .authorizeHttpRequests((authorizeRequests -> authorizeRequests
//                        .requestMatchers("/", "/member/**").permitAll()
//                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 관련 경로 허용
//                        .requestMatchers("/", "/**").permitAll()));
//
//        http
//                .formLogin(login -> login
//                        .loginPage("/")
//                        .loginProcessingUrl("/member/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/member/successLogin")
//                        .failureForwardUrl("/member/failureLogin")
//                        .permitAll());
//
//        return http.build();
//    }

    //패스워드 암호화로 사용할 bean
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

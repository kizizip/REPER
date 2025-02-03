//package com.d109.reper.jwt;
//
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.JWT;
//import com.d109.reper.controller.UserController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//import java.util.Date;
//
//import static org.springframework.security.config.Elements.JWT;
//
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//
//    @Override
//    //Authentication 객체 만들어서 리턴해야 한다. (AuthenticationManaer를 통해서)
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        log.info("JwtAuthenticationFilter 로그인: 진입");
//
//        //로그인 요청시 들어온 데이터를 객체로 변환
//        ObjectMapper om = new ObjectMapper();
//        UserController.LoginRequest userLoginRequest = null;
//        try {
//            userLoginRequest = om.readValue(request.getInputStream(), UserController.LoginRequest.class);
//        } catch (Exception e ) {
//            e.printStackTrace();
//        }
//
//        //해당 객체로 로그인 시도를 위한 유저네임패스워드 authenticationToken 생성
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(
//                        userLoginRequest.getEmail(),
//                        userLoginRequest.getPassword()
//                );
//
////        Authentication authentication = authenticationManager.authenticate(authenticationToken);
////
////        //위 영역이 성공했다면, session 영역에 authentication 객체가 저장된다 -> 로그인이 성공
////        return authentication;
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(authenticationToken);
//            return authentication;
//        } catch (AuthenticationException e) {
//            log.error("Authentication failed", e);
//            try {
//                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication failed");
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//            return null;
//        }
//    }
//
//    @Override
//    //로그인 인증 성공하면 들어오는 메소드
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
//        throws IOException, ServletException {
//        //Authentication에 있는 정보로 JWT Token 생성해서 response에 담아주기
//        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
//        String jwtToken = com.auth0.jwt.JWT.create()
//                .withSubject(principalDetails.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
//                .withClaim("email", principalDetails.getUser().getEmail())
////                .withClaim("username", principalDetails.getUser().getUsername())
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
//
////        logger.info("JWT Token: {}", jwtToken);
//        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
//    }
//}
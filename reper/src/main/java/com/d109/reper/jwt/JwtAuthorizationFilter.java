//package com.d109.reper.jwt;
//
//import com.auth0.jwt.JWT;
//import com.d109.reper.domain.User;
//import com.d109.reper.repository.UserRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import com.auth0.jwt.algorithms.Algorithm;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import java.io.IOException;
//
//
//public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
//
//    private UserRepository userRepository;
//
//    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
//        super(authenticationManager);
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//        throws IOException, ServletException {
//        logger.info("JwtAuthorizationFilter 인가: 진입");
//
//        //JWT 토큰이 헤더가 존재한다면, if문 안으로 들어가서 토큰 검증 시작
//        String header = request.getHeader(JwtProperties.HEADER_STRING);
//        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
//            System.out.println("header: "+ header);
//            String token = header.replace(JwtProperties.TOKEN_PREFIX, "");
//
//            //토큰 자체 검증
//            //토큰 자체에 인가 정보가 담겨 있기에, AuthenticationManager가 필요 없다.
//
//            //내가 가지고 있는 secret키를 사용해, JWT 토큰이 올바른지 확인
//            String email = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
//                    .getClaim("email").asString();
//
//            if (email != null) {
//                User user = userRepository.findByEmail(email).orElseThrow();
//
//                //인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
//                //아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장하기
//                PrincipalDetails principalDetails = new PrincipalDetails(user);
//                Authentication authentication = new UsernamePasswordAuthenticationToken(
//                        principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용
//                        null, //패스워드는 모르니까  null 처리
//                        principalDetails.getAuthorities()
//                );
//
//                //강제로 시큐리티의 세션에 접근하여 값 저장
//                //서명을 통해 authentication을 객체로 만들어준다.
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        //다음 과정으로 넘어간다.
//        //JWT 토큰이 맞다면 security 세션에 값이 있는 상태에서 로그인 상태로 다음 절차 진행
//        //JWT 토큰 인증 실패했다면, Security 세션이 빈 상태로 다음 절차 진행 -> 에러 발생
//        doFilter(request, response, chain);
//    }
//}

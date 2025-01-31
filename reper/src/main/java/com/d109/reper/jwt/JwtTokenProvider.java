//package com.d109.reper.jwt;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//    private final String secretKey = "mySecretKey"; //실제 서비스에서는 외부에서 관리해야 함
//
//    //JWT 생성
//    public String createToken(String email, String role) {
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + 3600000); //1시간 만료
//
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("role", role)
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    //JWT 유효성 검증
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    //JWT에서 사용자 이메일 추출
//    public String getEmailFromToken(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//}

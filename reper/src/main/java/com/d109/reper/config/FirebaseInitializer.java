package com.d109.reper.config;

import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service  // 🔥 Firebase 초기화를 담당하는 서비스
public class FirebaseInitializer {

    @PostConstruct  // 🚀 애플리케이션 시작 시 한 번 실행
    public void initFirebase() {
        try {
            String firebaseJsonBase64 = System.getenv("FIREBASE_JSON");  // 🔥 환경 변수에서 Base64 인코딩된 JSON 가져오기
            if (firebaseJsonBase64 == null || firebaseJsonBase64.isEmpty()) {
                throw new IllegalStateException("❌ 환경 변수 FIREBASE_JSON_BASE64가 설정되지 않았습니다.");
            }

            // 🔥 Base64 디코딩 추가
            byte[] decodedJson = Base64.getDecoder().decode(firebaseJsonBase64);

            if (FirebaseApp.getApps().isEmpty()) {  // Firebase 초기화가 되어있지 않다면 수행
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(decodedJson)
                );
                FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase 초기화 완료!");
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Firebase 초기화 실패", e);
        }
    }
}

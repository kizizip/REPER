package com.d109.reper.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service  // 🔥 Firebase 초기화를 담당하는 서비스
public class FirebaseInitializer {

    @Value("${firebase.credential}")
    private String firebaseJson;  // 🔥 application.yml에서 환경 변수로 Firebase JSON 가져오기

    @PostConstruct  // 🚀 애플리케이션 시작 시 한 번 실행
    public void initFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {  // Firebase 초기화가 되어있지 않다면 수행
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(firebaseJson.getBytes())
                );
                FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase 초기화 완료!");
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Firebase 초기화 실패", e);
        }
    }
}
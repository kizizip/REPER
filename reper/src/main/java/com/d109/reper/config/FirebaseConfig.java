package com.d109.reper.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = new FileInputStream("src/main/resources/firebase/reper-7e5b4-firebase-adminsdk-fbsvc-ee6581830a.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("FirebaseApp 초기화 완료: " + app.getName());
            return app;
        } else {
            System.out.println("FirebaseApp 이미 초기화됨: " + FirebaseApp.getApps().get(0).getName());
            return FirebaseApp.getApps().get(0);
        }
    }
}

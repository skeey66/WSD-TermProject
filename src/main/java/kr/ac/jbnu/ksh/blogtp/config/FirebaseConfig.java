package kr.ac.jbnu.ksh.blogtp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.social.firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    @Value("${app.social.firebase.credentials-base64:}")
    private String credentialsBase64;

    @PostConstruct
    public void init() {
        if (FirebaseApp.getApps().isEmpty()) {
            if (credentialsBase64 == null || credentialsBase64.isBlank()) {
                log.warn("Firebase enabled, but credentials-base64 is empty. Firebase login will fail.");
                return;
            }
            try {
                byte[] decoded = Base64.getDecoder().decode(credentialsBase64);
                GoogleCredentials creds = GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(creds)
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized.");
            } catch (Exception e) {
                log.error("Failed to init FirebaseApp", e);
            }
        }
    }
}

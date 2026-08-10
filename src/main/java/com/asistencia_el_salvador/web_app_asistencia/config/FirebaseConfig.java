package com.asistencia_el_salvador.web_app_asistencia.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
            if (credentialsPath == null) {
                credentialsPath = "/app/appasistencia-b2150-firebase-adminsdk-fbsvc-ca50f80205.json";
            }

            InputStream serviceAccount = null;
            File credentialsFile = new File(credentialsPath);

            if (credentialsFile.exists()) {
                serviceAccount = new FileInputStream(credentialsFile);
                log.info("✓ Credenciales de Firebase cargadas desde: {}", credentialsPath);
            } else {
                serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream("appasistencia-b2150-firebase-adminsdk-fbsvc-ca50f80205.json");
                if (serviceAccount != null) {
                    log.info("✓ Credenciales de Firebase cargadas desde resources");
                }
            }

            if (serviceAccount == null) {
                log.warn("⚠ Credenciales de Firebase no encontradas. Firebase no estará disponible.");
                return null; // Bean existe pero es null — se maneja abajo en FirebaseStorageService
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("appasistencia-b2150.firebasestorage.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("✓ Firebase inicializado correctamente");
                return app;
            }
            return FirebaseApp.getInstance();

        } catch (Exception e) {
            log.error("⚠ Error al inicializar Firebase: {}", e.getMessage());
            return null;
        }
    }
}
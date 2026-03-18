package com.asistencia_el_salvador.web_app_asistencia.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
            if (credentialsPath == null) {
                credentialsPath = "/app/appasistencia-b2150-firebase-adminsdk-fbsvc-ca50f80205.json";
            }
            System.out.println("=== DEBUG FIREBASE ===");
            System.out.println("FIREBASE_CREDENTIALS_PATH = " + credentialsPath);
            System.out.println("====================");

            // Imprimir TODAS las variables de entorno (opcional)
            System.out.println("All environment variables:");
            System.getenv().forEach((key, value) -> {
                if (key.contains("FIREBASE") || key.contains("firebase")) {
                    System.out.println(key + " = " + value);
                }
            });





            InputStream serviceAccount = null;
            File credentialsFile = new File(credentialsPath);

            if (credentialsFile.exists()) {
                serviceAccount = new FileInputStream(credentialsFile);
                System.out.println("✓ Credenciales de Firebase cargadas desde: " + credentialsPath);
            } else {
                serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream("appasistencia-b2150-firebase-adminsdk-fbsvc-ca50f80205.json");

                if (serviceAccount != null) {
                    System.out.println("✓ Credenciales de Firebase cargadas desde resources");
                }
            }

            if (serviceAccount == null) {
                System.out.println("⚠ ADVERTENCIA: Credenciales de Firebase no encontradas.");
                System.out.println("  Firebase no estará disponible en este entorno.");
                System.out.println("  La aplicación continuará funcionando sin Firebase.");
                return; // Salir sin lanzar excepción
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("appasistencia-b2150.firebasestorage.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✓ Firebase inicializado correctamente");
            }

        } catch (Exception e) {
            System.err.println("⚠ Error al inicializar Firebase: " + e.getMessage());
            System.err.println("  La aplicación continuará funcionando sin Firebase.");
            // NO lanzar RuntimeException para permitir que la app inicie
        }
    }
}
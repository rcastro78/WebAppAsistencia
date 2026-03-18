FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar el JAR
COPY target/*.jar app.jar

# Crear directorio para configuración
RUN mkdir -p /app/config

# Copiar el JSON de Firebase en un directorio separado
COPY appasistencia-b2150-firebase-adminsdk-fbsvc-ca50f80205.json /app/config/firebase-credentials.json

# Variable de entorno para la ruta
ENV FIREBASE_CREDENTIALS_PATH=/app/config/firebase-credentials.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
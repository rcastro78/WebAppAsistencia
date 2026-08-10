package com.asistencia_el_salvador.web_app_asistencia.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseStorageService.class);

    private final Bucket bucket;

    public FirebaseStorageService(@Nullable FirebaseApp firebaseApp) {
        if (firebaseApp != null) {
            this.bucket = StorageClient.getInstance(firebaseApp).bucket();
            log.info("✓ FirebaseStorageService listo con bucket: {}", bucket.getName());
        } else {
            this.bucket = null;
            log.warn("⚠ FirebaseStorageService creado sin Firebase disponible. Las operaciones de almacenamiento fallarán hasta que se configuren credenciales.");
        }
    }

    /**
     * Sube un archivo a Firebase Storage con un prefijo personalizado
     * @param file archivo a subir
     * @param prefix prefijo para el nombre del archivo (ej: "dui_frente_12345678")
     * @return URL pública del archivo
     */
    public String uploadFile(MultipartFile file, String prefix) throws IOException {
        requireBucket();

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        blob.createAcl(com.google.cloud.storage.Acl.of(
                com.google.cloud.storage.Acl.User.ofAllUsers(),
                com.google.cloud.storage.Acl.Role.READER
        ));

        return String.format("https://storage.googleapis.com/%s/%s",
                bucket.getName(), fileName);
    }

    public byte[] downloadFile(String fileName) throws IOException {
        requireBucket();
        Blob blob = bucket.get(fileName);
        if (blob == null) {
            throw new IOException("Archivo no encontrado: " + fileName);
        }
        return blob.getContent();
    }

    public boolean deleteFile(String fileName) {
        if (bucket == null) return false;
        Blob blob = bucket.get(fileName);
        if (blob == null) {
            return false;
        }
        return blob.delete();
    }

    public boolean deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        return deleteFile(fileName);
    }

    private void requireBucket() throws IOException {
        if (bucket == null) {
            throw new IOException("Firebase Storage no está configurado en este entorno.");
        }
    }
}
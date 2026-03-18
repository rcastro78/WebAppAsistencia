package com.asistencia_el_salvador.web_app_asistencia.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final Bucket bucket;

    public FirebaseStorageService() {
        this.bucket = StorageClient.getInstance().bucket();
    }

    /**
     * Sube un archivo a Firebase Storage con un prefijo personalizado
     * @param file archivo a subir
     * @param prefix prefijo para el nombre del archivo (ej: "dui_frente_12345678")
     * @return URL pública del archivo
     */
    public String uploadFile(MultipartFile file, String prefix) throws IOException {
        // Obtener extensión del archivo original
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar nombre único: prefix_uuid.extension
        String fileName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Subir archivo a Firebase Storage
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        // Hacer el archivo público para que pueda ser accedido por URL
        blob.createAcl(com.google.cloud.storage.Acl.of(
                com.google.cloud.storage.Acl.User.ofAllUsers(),
                com.google.cloud.storage.Acl.Role.READER
        ));

        // Retornar URL pública del archivo
        return String.format("https://storage.googleapis.com/%s/%s",
                bucket.getName(), fileName);
    }

    /**
     * Descarga un archivo de Firebase Storage
     * @param fileName nombre del archivo
     * @return contenido del archivo en bytes
     */
    public byte[] downloadFile(String fileName) throws IOException {
        Blob blob = bucket.get(fileName);
        if (blob == null) {
            throw new IOException("Archivo no encontrado: " + fileName);
        }
        return blob.getContent();
    }

    /**
     * Elimina un archivo de Firebase Storage
     * @param fileName nombre del archivo
     * @return true si se eliminó correctamente
     */
    public boolean deleteFile(String fileName) {
        Blob blob = bucket.get(fileName);
        if (blob == null) {
            return false;
        }
        return blob.delete();
    }

    /**
     * Elimina un archivo usando su URL completa
     * @param fileUrl URL completa del archivo
     * @return true si se eliminó correctamente
     */
    public boolean deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        // Extraer el nombre del archivo de la URL
        // URL formato: https://storage.googleapis.com/bucket-name/filename
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        return deleteFile(fileName);
    }
}
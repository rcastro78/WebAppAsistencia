package com.asistencia_el_salvador.web_app_asistencia.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class PdfGeneratorService {

    /**
     * Genera un PDF con una página por cada URL de imagen
     * @param imageUrls Lista de URLs de las imágenes (DUIs)
     * @return byte[] conteniendo el PDF generado
     */
    public byte[] generatePdfFromImageUrls(List<String> imageUrls) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (String imageUrl : imageUrls) {
                try {
                    // Descargar la imagen desde la URL
                    Image image = downloadImageFromUrl(imageUrl);

                    // Ajustar la imagen al tamaño de la página
                    image.scaleToFit(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 40);
                    image.setAlignment(Image.ALIGN_CENTER);

                    // Agregar la imagen al documento
                    document.add(image);

                    // Agregar nueva página si no es la última imagen
                    if (imageUrls.indexOf(imageUrl) < imageUrls.size() - 1) {
                        document.newPage();
                    }

                } catch (Exception e) {
                    System.err.println("Error procesando imagen: " + imageUrl);
                    e.printStackTrace();
                    // Continuar con la siguiente imagen
                }
            }

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return outputStream.toByteArray();
    }

    /**
     * Descarga una imagen desde una URL
     */
    private Image downloadImageFromUrl(String imageUrl) throws IOException, DocumentException {
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Image.getInstance(imageBytes);
        }
    }
}

package com.asistencia_el_salvador.web_app_asistencia.service;

import io.nayuki.qrcodegen.QrCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class QRCodeService {

    public String generateQrOnImage(String text, String backgroundImagePath) throws IOException {
        // 1. Generar QR code
        QrCode qr = QrCode.encodeText(text, QrCode.Ecc.MEDIUM);
        int scale = 5; // tamaño del QR

        int qrSize = qr.size * scale;
        BufferedImage qrImage = new BufferedImage(qrSize, qrSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = qrImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, qrSize, qrSize);
        g.setColor(Color.BLUE);

        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
        g.dispose();

        // 2. Cargar la imagen de fondo
        BufferedImage background = ImageIO.read(getClass().getResourceAsStream(backgroundImagePath));

        // 3. Redimensionar la imagen de fondo para que sea más pequeña
        // Calculamos que el logo sea aproximadamente 20-25% del tamaño del QR
        int logoSize = qrSize / 4; // Ajusta este valor según prefieras (4 = 25%, 5 = 20%, 3 = 33%)
        int offset = logoSize / 10; // Margen blanco alrededor del logo (10% del tamaño del logo)

        // Tamaño total incluyendo el offset
        int logoWithOffset = logoSize + (offset * 2);

        // Redimensionar el logo manteniendo las proporciones
        Image scaledLogo = background.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
        BufferedImage resizedLogo = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedLogo.createGraphics();
        g2d.drawImage(scaledLogo, 0, 0, null);
        g2d.dispose();

        // 4. Crear una imagen para el logo con offset (fondo blanco)
        BufferedImage logoWithBackground = new BufferedImage(logoWithOffset, logoWithOffset, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gLogo = logoWithBackground.createGraphics();

        // Fondo blanco con bordes redondeados opcionales
        gLogo.setColor(Color.WHITE);
        gLogo.fillRoundRect(0, 0, logoWithOffset, logoWithOffset, 10, 10); // 10 = radio de bordes redondeados

        // Dibujar el logo centrado con el offset
        gLogo.drawImage(resizedLogo, offset, offset, null);
        gLogo.dispose();

        // 5. Combinar QR y logo
        BufferedImage combined = new BufferedImage(qrSize, qrSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = combined.createGraphics();

        // Dibujar el QR
        g2.drawImage(qrImage, 0, 0, null);

        // Dibujar el logo con offset en el centro del QR
        int x = (qrSize - logoWithOffset) / 2;
        int y = (qrSize - logoWithOffset) / 2;
        g2.drawImage(logoWithBackground, x, y, null);
        g2.dispose();

        // 6. Convertir a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        return "data:image/png;base64," + base64;
    }
}
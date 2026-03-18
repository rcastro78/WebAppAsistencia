package com.asistencia_el_salvador.web_app_asistencia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Email simple de texto
    public void enviarEmailSimple(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);

        mailSender.send(email);
    }

    // Email con formato HTML
    public void enviarEmailHtml(String destinatario, String asunto, String contenidoHtml)
            throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true); // true indica que es HTML

        mailSender.send(mensaje);
    }

    public void enviarEmailHtml(String destinatario,String cc, String asunto, String contenidoHtml)
            throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setCc(cc);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true); // true indica que es HTML

        mailSender.send(mensaje);
    }

    // Email específico para nuevo afiliado
    public void enviarEmailBienvenidaAfiliado(String nombreAfiliado, String emailAfiliado,
                                              String dui, String pass) {
        String asunto = "Bienvenido a Asistencia El Salvador";
        String contenido = String.format("""
            Estimado/a %s,
            
            ¡Bienvenido a Asistencia El Salvador!
            
            Tu registro se ha completado exitosamente. Ahora eres parte de nuestra comunidad.
            Espera a recibir nuestra confirmación que tu cuenta se encuentra activa.
            Si tienes alguna pregunta, no dudes en contactarnos.
            Usuario: %s
            Clave: %s
            Saludos cordiales,
            Equipo de Asistencia El Salvador
            """, nombreAfiliado, dui, pass);

        enviarEmailSimple(emailAfiliado, asunto, contenido);
    }

    public void enviarEmailBienvenidaDoctor(String nombreDoctor, String emailDoctor,
                                            String dui, String pass) {
        String asunto = "Bienvenido al equipo médico - Asistencia El Salvador";
        String contenido = String.format("""
        Estimado/a Dr./Dra. %s,

        ¡Bienvenido/a al equipo médico de Asistencia El Salvador!

        Su registro como profesional de salud se ha completado exitosamente.
        A continuación encontrará sus datos de acceso al sistema:

        Usuario: %s
        Password: %s

        Su cuenta será activada en breve por el administrador.

        Si tiene alguna consulta, no dude en contactarnos.

        Saludos cordiales,
        Equipo de Asistencia El Salvador
        """, nombreDoctor, dui, pass);

        enviarEmailSimple(emailDoctor, asunto, contenido);
    }

    public void enviarEmailBienvenidaVendedor(String nombreVendedor, String emailVendedor,
                                              String usuario, String pass) {
        String asunto = "Bienvenido al Equipo de Vendedores - Asistencia El Salvador";
        String contenido = String.format("""
        Estimado/a %s,
        
        ¡Bienvenido/a al equipo de vendedores de Asistencia El Salvador!
        
        Tu registro como vendedor se ha completado exitosamente.
        A continuación te compartimos tus credenciales de acceso:
        
        Usuario: %s
        Clave: %s
        
        Por favor, guarda esta información en un lugar seguro.
        Si tienes alguna pregunta o necesitas asistencia, no dudes en contactarnos.
        
        Saludos cordiales,
        Equipo de Asistencia El Salvador
        """, nombreVendedor, usuario, pass);

        enviarEmailSimple(emailVendedor, asunto, contenido);
    }
    // ==================== MÉTODOS DE WHATSAPP ====================

    /**
     * Genera un enlace de WhatsApp con mensaje predefinido
     * @param numeroDestino Número en formato internacional sin +: 50312345678
     * @param mensaje Mensaje pre-escrito
     * @return URL de WhatsApp
     */
    public String generarEnlaceWhatsApp(String numeroDestino, String mensaje) {
        try {
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString());
            return String.format("https://wa.me/%s?text=%s", numeroDestino, mensajeCodificado);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al codificar mensaje de WhatsApp", e);
        }
    }

    /**
     * Genera enlace de WhatsApp para contactar a la empresa
     * @param mensaje Mensaje pre-escrito
     * @return URL de WhatsApp
     */
    public String generarEnlaceWhatsAppEmpresa(String mensaje) {
        return generarEnlaceWhatsApp("", mensaje);
    }

    /**
     * Genera enlace de bienvenida por WhatsApp para nuevo afiliado
     * @param nombreAfiliado Nombre del afiliado
     * @return URL de WhatsApp lista para usar
     */
    public String generarEnlaceWhatsAppBienvenida(String nombreAfiliado) {
        String mensaje = String.format(
                "Hola, soy %s y acabo de registrarme en Asistencia El Salvador. " +
                        "Me gustaría recibir más información sobre mi cuenta.",
                nombreAfiliado
        );
        return generarEnlaceWhatsAppEmpresa(mensaje);
    }

    /**
     * Envía email de bienvenida con enlace de WhatsApp incluido
     * @param nombreAfiliado Nombre del afiliado
     * @param emailAfiliado Email del afiliado
     */
    public void enviarEmailBienvenidaConWhatsApp(String nombreAfiliado, String emailAfiliado)
            throws MessagingException {
        String enlaceWhatsApp = generarEnlaceWhatsAppBienvenida(nombreAfiliado);

        String asunto = "Bienvenido a Asistencia El Salvador";
        String contenidoHtml = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #25D366; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .whatsapp-btn { 
                        display: inline-block;
                        background-color: #25D366;
                        color: white;
                        padding: 12px 30px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: bold;
                    }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>¡Bienvenido a Asistencia El Salvador!</h2>
                    </div>
                    <div class="content">
                        <p>Estimado/a <strong>%s</strong>,</p>
                        
                        <p>¡Tu registro se ha completado exitosamente! Ahora eres parte de nuestra comunidad.</p>
                        
                        <p>Espera a recibir nuestra confirmación de que tu cuenta se encuentra activa, 
                        en ella te informaremos sobre tu usuario y clave del sistema.</p>
                        
                        <p>Si tienes alguna pregunta inmediata, puedes contactarnos directamente por WhatsApp:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="whatsapp-btn">
                                📱 Contáctanos por WhatsApp
                            </a>
                        </div>
                        
                        <p>También puedes escribirnos respondiendo a este correo.</p>
                        
                        <p>Saludos cordiales,<br>
                        <strong>Equipo de Asistencia El Salvador</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este es un correo automático, por favor no responder directamente.</p>
                    </div>
                </div>
            </body>
            </html>
            """, nombreAfiliado, enlaceWhatsApp);

        enviarEmailHtml(emailAfiliado, asunto, contenidoHtml);
    }

    /**
     * Genera un enlace de WhatsApp para soporte
     * @return URL de WhatsApp para soporte
     */
    public String generarEnlaceWhatsAppSoporte() {
        String mensaje = "Hola, necesito ayuda con mi cuenta de Asistencia El Salvador.";
        return generarEnlaceWhatsAppEmpresa(mensaje);
    }

    /**
     * Genera un enlace de WhatsApp para consultas generales
     * @param tipoConsulta Tipo de consulta
     * @return URL de WhatsApp
     */
    public String generarEnlaceWhatsAppConsulta(String tipoConsulta) {
        String mensaje = String.format("Hola, tengo una consulta sobre: %s", tipoConsulta);
        return generarEnlaceWhatsAppEmpresa(mensaje);
    }
}

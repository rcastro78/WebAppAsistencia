package com.asistencia_el_salvador.web_app_asistencia.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.asistencia_el_salvador.web_app_asistencia.model.WompiWebhookResponse;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class WompiWebhookService {

    private static final Logger log = LoggerFactory.getLogger(WompiWebhookService.class);

    private String apiSecret;

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final AfiliadoService afiliadoService;

    public WompiWebhookService(ObjectMapper objectMapper,
                               EmailService emailService,
                               AfiliadoService afiliadoService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.afiliadoService = afiliadoService;
    }

    // Procesar webhook con validación de hash (recomendado para producción)
    public void procesarWebhook(String payload, String wompiHash) {

        // Validar el hash
        if (!validarHash(payload, wompiHash)) {
            log.error("Hash de webhook inválido");
            throw new RuntimeException("Hash inválido - webhook no confiable");
        }

        log.info("Hash validado correctamente");

        try {
            // Parsear el JSON
            WompiWebhookResponse webhook = objectMapper.readValue(payload, WompiWebhookResponse.class);
            procesarTransaccion(webhook);

        } catch (Exception e) {
            log.error("Error parseando webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Error procesando webhook", e);
        }
    }

    // Procesar webhook simple sin validación (solo para desarrollo)
    public void procesarWebhookSimple(WompiWebhookResponse webhook) {
        log.info("Procesando webhook sin validación de hash");
        procesarTransaccion(webhook);
    }

    // Validar el hash HMAC SHA256 del webhook
    private boolean validarHash(String payload, String wompiHash) {
        if (wompiHash == null || wompiHash.isEmpty()) {
            log.warn("No se recibió el header wompi_hash");
            return false;
        }

        try {
            String hashCalculado = calcularHmacSha256(payload, apiSecret);
            boolean esValido = hashCalculado.equalsIgnoreCase(wompiHash);

            if (!esValido) {
                log.error("Hash no coincide. Recibido: {}, Calculado: {}", wompiHash, hashCalculado);
            }

            return esValido;

        } catch (Exception e) {
            log.error("Error calculando hash: {}", e.getMessage(), e);
            return false;
        }
    }

    // Calcular HMAC SHA256
    private String calcularHmacSha256(String data, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);

        byte[] hashBytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hashBytes);
    }

    // Procesar la transacción según su resultado
    private void procesarTransaccion(WompiWebhookResponse webhook) {

        // Verificar si es transacción de prueba
        if (Boolean.FALSE.equals(webhook.getEsProductiva())) {
            log.info("Transacción de PRUEBA detectada - ID: {}", webhook.getIdTransaccion());
        }

        String resultado = webhook.getResultadoTransaccion();

        switch (resultado) {
            case "ExitosaAprobada":
                manejarTransaccionExitosa(webhook);
                break;
            case "Fallida":
                manejarTransaccionFallida(webhook);
                break;
            case "Rechazada":
                manejarTransaccionRechazada(webhook);
                break;
            default:
                log.warn("Resultado de transacción no manejado: {}", resultado);
        }
    }

    private void manejarTransaccionExitosa(WompiWebhookResponse webhook) {
        log.info("Procesando transacción exitosa");
        log.info("ID Transacción: {}", webhook.getIdTransaccion());
        log.info("Monto: ${}", webhook.getMonto());
        log.info("Código Autorización: {}", webhook.getCodigoAutorizacion());

        try {
            // Obtener datos del cliente
            String nombreCliente = webhook.getCliente() != null ?
                    webhook.getCliente().getNombre() : "Cliente";
            String emailCliente = webhook.getCliente() != null ?
                    webhook.getCliente().getEmail() : null;

            String nombreProducto = webhook.getEnlacePago() != null ?
                    webhook.getEnlacePago().getNombreProducto() : "Producto";

            // Enviar email de confirmación
            if (emailCliente != null && !emailCliente.isEmpty()) {
                String asunto = "Confirmación de pago - " + nombreProducto;
                String mensaje = String.format(
                        "Estimado/a %s,\n\n" +
                                "Tu pago ha sido procesado exitosamente.\n\n" +
                                "Detalles de la transacción:\n" +
                                "- Producto: %s\n" +
                                "- Monto: $%.2f\n" +
                                "- ID Transacción: %s\n" +
                                "- Código de Autorización: %s\n" +
                                "- Forma de pago: %s\n\n" +
                                "Gracias por tu compra.\n\n" +
                                "Saludos,\n" +
                                "Equipo de Asistencia El Salvador",
                        nombreCliente,
                        nombreProducto,
                        webhook.getMonto(),
                        webhook.getIdTransaccion(),
                        webhook.getCodigoAutorizacion(),
                        webhook.getFormaPagoUtilizada()
                );

                emailService.enviarEmailSimple(emailCliente, asunto, mensaje);
                log.info("Email de confirmación enviado a: {}", emailCliente);
            }

            // Aquí puedes actualizar tu base de datos
            // Por ejemplo: actualizar el estado del pedido, registrar el afiliado, etc.
            String identificadorComercio = webhook.getEnlacePago() != null ?
                    webhook.getEnlacePago().getIdentificadorEnlaceComercio() : null;

            if (identificadorComercio != null) {
                log.info("Procesando orden de comercio: {}", identificadorComercio);
                // afiliadoService.activarAfiliado(identificadorComercio);
            }

        } catch (Exception e) {
            log.error("Error enviando email o procesando transacción: {}", e.getMessage(), e);
        }
    }

    private void manejarTransaccionFallida(WompiWebhookResponse webhook) {
        log.info("Transacción fallida - ID: {}", webhook.getIdTransaccion());

        try {
            String emailCliente = webhook.getCliente() != null ?
                    webhook.getCliente().getEmail() : null;

            if (emailCliente != null && !emailCliente.isEmpty()) {
                String asunto = "Pago no procesado";
                String mensaje = String.format(
                        "Estimado/a cliente,\n\n" +
                                "Lamentamos informarte que tu pago no pudo ser procesado.\n\n" +
                                "ID de transacción: %s\n\n" +
                                "Por favor, intenta nuevamente o contacta con nuestro soporte.\n\n" +
                                "Saludos,\n" +
                                "Equipo de Asistencia El Salvador",
                        webhook.getIdTransaccion()
                );

                emailService.enviarEmailSimple(emailCliente, asunto, mensaje);
            }

        } catch (Exception e) {
            log.error("Error enviando email de transacción fallida: {}", e.getMessage(), e);
        }
    }

    private void manejarTransaccionRechazada(WompiWebhookResponse webhook) {
        log.info("Transacción rechazada - ID: {}", webhook.getIdTransaccion());
        // Similar a manejarTransaccionFallida
    }
}
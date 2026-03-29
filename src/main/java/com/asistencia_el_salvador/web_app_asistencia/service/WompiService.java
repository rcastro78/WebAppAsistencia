package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Transaccion;
import com.asistencia_el_salvador.web_app_asistencia.repository.TransaccionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class WompiService {

    @Value("${wompi.secret-key}")
    private String wompiSecretKey;

    @Value("${wompi.public-key}")
    private String wompiPublicKey;

    @Value("${wompi.api.url}")
    private String wompiApiUrl;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Verificar firma del webhook (SEGURIDAD)
    public boolean verificarFirma(Map<String, Object> payload, String firmaRecibida) {
        try {
            String timestamp = payload.get("timestamp").toString();
            String transactionId = ((Map<String, Object>) payload.get("data")).get("id").toString();
            String status = ((Map<String, Object>) payload.get("data")).get("status").toString();

            // Concatenar según documentación de Wompi
            String cadena = timestamp + "." + transactionId + "." + status + "." +
                    payload.get("environment");

            // Calcular HMAC SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(wompiSecretKey.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(cadena.getBytes());
            String firmaCalculada = Base64.getEncoder().encodeToString(hash);

            return firmaCalculada.equals(firmaRecibida);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Procesar webhook
    public void procesarWebhook(Map<String, Object> payload, String evento) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> transaction = (Map<String, Object>) data.get("transaction");

            String transactionId = transaction.get("id").toString();
            String status = transaction.get("status").toString();
            String reference = transaction.get("reference").toString();

            // Buscar o crear transacción
            Transaccion trans = transaccionRepository.findByWompiTransactionId(transactionId)
                    .orElse(new Transaccion());

            trans.setWompiTransactionId(transactionId);
            trans.setWompiReference(reference);
            trans.setStatus(status);
            trans.setAmount(Double.parseDouble(transaction.get("amount_in_cents").toString()));
            trans.setCurrency(transaction.get("currency").toString());
            trans.setPaymentMethod(transaction.get("payment_method_type").toString());
            trans.setFechaActualizacion(LocalDateTime.now());
            trans.setWompiResponse(objectMapper.writeValueAsString(payload));

            if (trans.getId() == null) {
                trans.setFechaCreacion(LocalDateTime.now());
            }

            transaccionRepository.save(trans);

            // Procesar según el estado
            switch (status) {
                case "APPROVED":
                    activarSuscripcion(trans);
                    break;
                case "DECLINED":
                    notificarPagoRechazado(trans);
                    break;
                case "ERROR":
                    registrarError(trans);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Consultar transacción en Wompi API
    public Map<String, Object> consultarTransaccion(String transactionId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = wompiApiUrl + "/transactions/" + transactionId;

            return restTemplate.getForObject(url, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void activarSuscripcion(Transaccion trans) {
        // Lógica para activar suscripción del usuario
        System.out.println("Activando suscripción para: " + trans.getCustomerEmail());
        // TODO: Actualizar estado de suscripción en tu BD
    }

    private void notificarPagoRechazado(Transaccion trans) {
        // Enviar email o notificación
        System.out.println("Pago rechazado: " + trans.getWompiReference());
    }

    private void registrarError(Transaccion trans) {
        System.err.println("Error en transacción: " + trans.getWompiTransactionId());
    }
}

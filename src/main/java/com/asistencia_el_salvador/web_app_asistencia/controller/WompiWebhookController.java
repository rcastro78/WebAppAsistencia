package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.WompiWebhookResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.WompiWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WompiWebhookController {

    private static final Logger log = LoggerFactory.getLogger(WompiWebhookController.class);

    private final WompiWebhookService wompiWebhookService;

    public WompiWebhookController(WompiWebhookService wompiWebhookService) {
        this.wompiWebhookService = wompiWebhookService;
    }

    @PostMapping("/wompi")
    public ResponseEntity<String> handleWompiWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "wompi_hash", required = false) String wompiHash) {

        log.info("Webhook recibido de Wompi");

        try {
            // Validar el hash del webhook
            wompiWebhookService.procesarWebhook(payload, wompiHash);

            return ResponseEntity.ok("Webhook procesado exitosamente");
        } catch (Exception e) {
            log.error("Error procesando webhook de Wompi: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando webhook");
        }
    }

    // Endpoint sin validación de hash (solo para desarrollo/pruebas)
    @PostMapping("/wompi/simple")
    public ResponseEntity<String> handleWompiWebhookSimple(@RequestBody WompiWebhookResponse webhook) {

        log.info("Webhook recibido - Transacción: {}, Resultado: {}",
                webhook, webhook.getResultadoTransaccion());

        try {
            wompiWebhookService.procesarWebhookSimple(webhook);
            return ResponseEntity.ok("Webhook procesado");
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno");
        }
    }
}
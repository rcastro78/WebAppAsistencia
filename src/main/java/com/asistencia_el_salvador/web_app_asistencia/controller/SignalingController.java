package com.asistencia_el_salvador.web_app_asistencia.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SignalingController {

    private static final Logger log = LoggerFactory.getLogger(SignalingController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Recibe la señal como String raw para NO perder ningún campo JSON
     * en la deserialización → re-serialización.
     * El broker simplemente reenvía el payload exactamente como llegó.
     */
    @MessageMapping("/signal/{roomId}")
    public void handleSignal(
            @DestinationVariable String roomId,
            @Payload String rawMessage          // ← raw JSON, sin deserializar
    ) {
        log.info("Signal | room={} | payload={}", roomId, rawMessage);

        // Reenvía a TODOS los suscriptores del topic (doctor + paciente).
        // El cliente filtra sus propios mensajes con msg.name === MI_USERNAME.
        messagingTemplate.convertAndSend(
                "/topic/signal/" + roomId,
                rawMessage
        );
    }
}
package com.asistencia_el_salvador.web_app_asistencia.config;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Mensaje de señalización WebRTC.
 * Usa @JsonAnySetter / @JsonAnyGetter para preservar campos
 * dinámicos (payload de SDP, ICE candidates, etc.) sin perderlos
 * en la deserialización → serialización del broker.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalMessage {

    private String type;    // join | offer | answer | ice-candidate | chat | hangup
    private String name;    // DUI del emisor  ← CAMPO CLAVE para el filtro self-message
    private String text;    // usado en chat
    private String sender;  // alias en chat

    /** Captura campos dinámicos: payload (SDP/ICE), etc. */
    private Map<String, Object> extra = new HashMap<>();

    // ── Getters / Setters estándar ──────────────────────────────────
    public String getType()   { return type; }
    public void   setType(String type) { this.type = type; }

    public String getName()   { return name; }
    public void   setName(String name) { this.name = name; }

    public String getText()   { return text; }
    public void   setText(String text) { this.text = text; }

    public String getSender() { return sender; }
    public void   setSender(String sender) { this.sender = sender; }

    // ── Campos dinámicos (payload SDP / ICE) ───────────────────────
    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    @JsonAnySetter
    public void setExtra(String key, Object value) { this.extra.put(key, value); }
}
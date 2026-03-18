package com.asistencia_el_salvador.web_app_asistencia.model;

public class SignalMessage {
    private String type;   // "offer", "answer", "ice-candidate", "join", "leave"
    private String roomId;
    private String senderId;
    private String targetId;
    private Object payload; // SDP o ICE candidate

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
package com.asistencia_el_salvador.web_app_asistencia.model;

public class MensajeError {

    private Integer fila;
    private String identificacion;
    private String mensaje;

    public MensajeError(Integer fila, String identificacion, String mensaje) {
        this.fila = fila;
        this.identificacion = identificacion;
        this.mensaje = mensaje;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }




}

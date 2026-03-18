package com.asistencia_el_salvador.web_app_asistencia.model;

public class SolicitudConsultaDTO {
    private String motivoConsulta;
    private String tipo; // "URGENTE" o "PROGRAMADA"

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
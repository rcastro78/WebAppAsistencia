package com.asistencia_el_salvador.web_app_asistencia.request;

public class ComercioLoginRequest {
    private String emailAsociado;
    private String contrasena;

    public String getEmailAsociado() {
        return emailAsociado;
    }

    public void setEmailAsociado(String emailAsociado) {
        this.emailAsociado = emailAsociado;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}

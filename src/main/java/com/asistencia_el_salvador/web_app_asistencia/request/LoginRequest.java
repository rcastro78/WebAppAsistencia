package com.asistencia_el_salvador.web_app_asistencia.request;

public class LoginRequest {
    private String dui;
    private String contrasena;

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
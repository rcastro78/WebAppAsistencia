package com.asistencia_el_salvador.web_app_asistencia.response;

public class UsuarioResponse {
    private String dui;
    private String nombre;
    private String apellido;
    private String email;
    private Integer rol;
    private Boolean activo;

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}


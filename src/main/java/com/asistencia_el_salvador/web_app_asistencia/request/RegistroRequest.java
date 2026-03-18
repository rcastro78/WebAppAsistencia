package com.asistencia_el_salvador.web_app_asistencia.request;


// Registro
public class RegistroRequest {
    private String dui;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private Integer rol;

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }
}

// Login


// Respuesta que usará tu app (sin contraseña)

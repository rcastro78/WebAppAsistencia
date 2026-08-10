package com.asistencia_el_salvador.web_app_asistencia.dto;


import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioProveedor;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UsuarioProveedorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idUsuarioProveedor;
    private String nit;
    private String dui;
    private String nombre;
    private String apellido;
    private String emailAsociado;
    private Integer estado;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String telefono;

    public UsuarioProveedorDTO() {}

    // Constructor desde la entidad
    public UsuarioProveedorDTO(UsuarioProveedor u) {
        this.idUsuarioProveedor = u.getIdUsuarioProveedor();
        this.nit               = u.getNitProveedor();
        this.dui               = u.getDui();
        this.nombre            = u.getNombre();
        this.apellido          = u.getApellido();
        this.emailAsociado     = u.getEmailAsociado();
        this.estado            = u.getEstado();
        this.createdAt         = u.getCreatedAt();
        this.deletedAt         = u.getDeletedAt();
        this.telefono          = u.getTelefono();
    }

    public Integer getIdUsuarioProveedor() { return idUsuarioProveedor; }
    public String getNit()                 { return nit; }
    public String getDui()                 { return dui; }
    public String getNombre()              { return nombre; }
    public String getApellido()            { return apellido; }
    public String getEmailAsociado()       { return emailAsociado; }
    public Integer getEstado()             { return estado; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getDeletedAt()    { return deletedAt; }
    public String getTelefono()            { return telefono; }
}
package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cliente_corporativo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "NRC")
        }
)
public class ClienteCorporativo implements Serializable {
    @Id
    @Column(name = "NIT", length = 20, nullable = false, unique = true)
    private String nit;
    @Column(name = "NRC", length = 20, nullable = false, unique = true)
    private String nrc;
    @Column(name = "nombreCliente", length = 100)
    private String nombreCliente;
    @Column(name = "emailContacto", length = 45)
    private String emailContacto;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "imagenURL", length = 200)
    private String imagenURL;
    @Column(name = "telefono", length = 15)
    private String telefono;
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

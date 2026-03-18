package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_comercio_afiliado")
@IdClass(UsuarioComercioPK.class)
public class UsuarioComercio {
    @Id
    @Column(name = "NIT", length = 45, nullable = false)
    private String nit;
    @Id
    @Column(name = "emailAsociado", length = 45, nullable = false)
    private String emailAsociado;
    @Column(name = "claveCifrada", length = 100)
    private String clave;
    @Column(name = "estado")
    private int estado;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getEmailAsociado() {
        return emailAsociado;
    }

    public void setEmailAsociado(String emailAsociado) {
        this.emailAsociado = emailAsociado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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
}

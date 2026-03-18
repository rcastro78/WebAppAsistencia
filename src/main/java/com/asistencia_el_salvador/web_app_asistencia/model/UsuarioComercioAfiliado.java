package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "usuario_comercio_afiliado")
@IdClass(UsuarioComercioAfiliadoId.class)
public class UsuarioComercioAfiliado implements Serializable {

    @Id
    @Column(name = "NIT", length = 45, nullable = false)
    private String nit;

    @Id
    @Column(name = "DUI", length = 45, nullable = false)
    private String dui;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "emailAsociado", length = 45)
    private String emailAsociado;

    @Column(name = "claveCifrada", length = 100)
    private String claveCifrada;

    @Column(name = "estado")
    private Integer estado = 0;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "telefono", length = 45)
    private String telefono;

    public UsuarioComercioAfiliado() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (estado == null) {
            estado = 0;
        }
    }

    // Getters
    public String getNit() {
        return nit;
    }

    public String getDui() {
        return dui;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmailAsociado() {
        return emailAsociado;
    }

    public String getClaveCifrada() {
        return claveCifrada;
    }

    public Integer getEstado() {
        return estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public String getTelefono() {
        return telefono;
    }

    // Setters
    public void setNit(String nit) {
        this.nit = nit;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmailAsociado(String emailAsociado) {
        this.emailAsociado = emailAsociado;
    }

    public void setClaveCifrada(String claveCifrada) {
        this.claveCifrada = claveCifrada;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioComercioAfiliado that = (UsuarioComercioAfiliado) o;
        return Objects.equals(nit, that.nit) && Objects.equals(dui, that.dui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nit, dui);
    }
}

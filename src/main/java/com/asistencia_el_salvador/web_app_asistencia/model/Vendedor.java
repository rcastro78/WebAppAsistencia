package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendedor")
public class Vendedor {

    @Id
    @Column(name = "dui", nullable = false, length = 50)
    private String dui;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "zona", length = 50)
    private String zona;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Vendedor() {}

    public Vendedor(String dui, String nombre, String apellido, String email,
                    String telefono, String zona, Boolean activo) {
        this.dui      = dui;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.telefono = telefono;
        this.zona     = zona;
        this.activo   = activo;
    }

    // ── Lifecycle callbacks ───────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Vendedor{" +
                "dui='"       + dui       + '\'' +
                ", nombre='"  + nombre    + '\'' +
                ", apellido='"+ apellido  + '\'' +
                ", email='"   + email     + '\'' +
                ", telefono='"+ telefono  + '\'' +
                ", zona='"    + zona      + '\'' +
                ", activo="   + activo    +
                ", createdAt="+ createdAt +
                ", updatedAt="+ updatedAt +
                ", deletedAt="+ deletedAt +
                '}';
    }
}

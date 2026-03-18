package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "med_doctor")
public class MedDoctor {

    @Id
    @Column(name = "DUI", nullable = false, length = 10)
    private String dui;

    @Column(name = "NIT", length = 45)
    private String nit;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "idEspecialidad")
    private Integer idEspecialidad;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "createdAt", updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ── Constructors ──────────────────────────────────────────

    public MedDoctor() {}

    public MedDoctor(String dui, String nit, String nombre, String apellido,
                     Integer idEspecialidad, String email, String telefono, Integer estado) {
        this.dui = dui;
        this.nit = nit;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idEspecialidad = idEspecialidad;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
    }

    // ── Lifecycle ─────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ── Getters & Setters ─────────────────────────────────────

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
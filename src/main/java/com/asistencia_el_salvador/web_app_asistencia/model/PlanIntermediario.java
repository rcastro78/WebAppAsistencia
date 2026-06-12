package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_intermediario")
public class PlanIntermediario {

    @Id
    @Column(name = "NITIntermediario", nullable = false, length = 25)
    private String nitIntermediario;

    @Column(name = "nombreIntermediario", length = 45)
    private String nombreIntermediario;

    @Column(name = "createdAt", updatable = false,
            columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "estado", columnDefinition = "int DEFAULT 1")
    private Integer estado;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (estado == null) {
            estado = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getNitIntermediario() {
        return nitIntermediario;
    }

    public void setNitIntermediario(String nitIntermediario) {
        this.nitIntermediario = nitIntermediario;
    }

    public String getNombreIntermediario() {
        return nombreIntermediario;
    }

    public void setNombreIntermediario(String nombreIntermediario) {
        this.nombreIntermediario = nombreIntermediario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    // Constructores
    public PlanIntermediario() {}

    public PlanIntermediario(String nitIntermediario, String nombreIntermediario,
                             LocalDateTime createdAt, LocalDateTime updatedAt,
                             Integer estado) {
        this.nitIntermediario = nitIntermediario;
        this.nombreIntermediario = nombreIntermediario;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.estado = estado;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_coparticipe")
public class PlanCoparticipe {

    @Id
    @Column(name = "NITCoparticipe", nullable = false, length = 25)
    private String nitCoparticipe;

    @Column(name = "nombreCoparticipe", length = 45)
    private String nombreCoparticipe;

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
    public String getNitCoparticipe() {
        return nitCoparticipe;
    }

    public void setNitCoparticipe(String nitCoparticipe) {
        this.nitCoparticipe = nitCoparticipe;
    }

    public String getNombreCoparticipe() {
        return nombreCoparticipe;
    }

    public void setNombreCoparticipe(String nombreCoparticipe) {
        this.nombreCoparticipe = nombreCoparticipe;
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
    public PlanCoparticipe() {}

    public PlanCoparticipe(String nitCoparticipe, String nombreCoparticipe,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           Integer estado) {
        this.nitCoparticipe = nitCoparticipe;
        this.nombreCoparticipe = nombreCoparticipe;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.estado = estado;
    }
}
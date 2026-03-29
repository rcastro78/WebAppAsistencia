package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipo_ventas")
public class EquipoVentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "duiSupervisor", length = 45)
    private String duiSupervisor;

    @Column(name = "duiVendedor", length = 45)
    private String duiVendedor;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "zonaCobertura", length = 200)
    private String zonaCobertura;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ── Constructors ────────────────────────────────────────────
    public EquipoVentas() {}

    public EquipoVentas(String duiSupervisor, String duiVendedor, Integer estado) {
        this.duiSupervisor = duiSupervisor;
        this.duiVendedor   = duiVendedor;
        this.estado        = estado;
    }

    // ── Getters & Setters ────────────────────────────────────────
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDuiSupervisor() { return duiSupervisor; }
    public void setDuiSupervisor(String duiSupervisor) { this.duiSupervisor = duiSupervisor; }

    public String getDuiVendedor() { return duiVendedor; }
    public void setDuiVendedor(String duiVendedor) { this.duiVendedor = duiVendedor; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = zonaCobertura;
    }

    @Override
    public String toString() {
        return "EquipoVentas{id=" + id +
                ", duiSupervisor='" + duiSupervisor + '\'' +
                ", duiVendedor='" + duiVendedor + '\'' +
                ", estado=" + estado +
                ", createdAt=" + createdAt + '}';
    }
}


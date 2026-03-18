package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculo_afiliado")
public class VehiculoAfiliado {

    @Id
    @Column(name = "placaVehiculo", length = 20, nullable = false)
    private String placaVehiculo;

    @Column(name = "DUIusuario", length = 45)
    private String duiUsuario;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "modelo", length = 45)
    private String modelo;

    @Column(name = "marca", length = 45)
    private String marca;

    @Column(name = "caracteristicas", length = 100)
    private String caracteristicas;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public VehiculoAfiliado() {}

    public VehiculoAfiliado(String placaVehiculo, String duiUsuario, Integer anio,
                            String modelo, String marca, String caracteristicas,
                            Integer estado) {
        this.placaVehiculo  = placaVehiculo;
        this.duiUsuario     = duiUsuario;
        this.anio           = anio;
        this.modelo         = modelo;
        this.marca          = marca;
        this.caracteristicas = caracteristicas;
        this.estado         = estado;
    }

    // ── Lifecycle callbacks ───────────────────────────────────────────────────

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.estado == null) {
            this.estado = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getDuiUsuario() {
        return duiUsuario;
    }

    public void setDuiUsuario(String duiUsuario) {
        this.duiUsuario = duiUsuario;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
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

    @Override
    public String toString() {
        return "VehiculoAfiliado{" +
                "placaVehiculo='" + placaVehiculo + '\'' +
                ", duiUsuario='" + duiUsuario + '\'' +
                ", anio=" + anio +
                ", modelo='" + modelo + '\'' +
                ", marca='" + marca + '\'' +
                ", estado=" + estado +
                '}';
    }
}

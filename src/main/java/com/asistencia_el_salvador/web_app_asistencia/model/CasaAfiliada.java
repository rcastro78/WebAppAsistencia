package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "casa_afiliada")
public class CasaAfiliada {

    @Id
    @Column(name = "idCasa")
    private Integer idCasa;

    @Column(name = "direccion", length = 45, unique = true)
    private String direccion;

    @Column(name = "DUIusuario", length = 45)
    private String duiUsuario;

    @Column(name = "numHabitaciones")
    private Integer numHabitaciones;

    @Column(name = "idDepto")
    private Integer idDepto;

    @Column(name = "idMunicipio")
    private Integer idMunicipio;

    @Column(name = "idDistrito")
    private Integer idDistrito;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    // ── Constructor vacío ──────────────────────────────────────────────────────
    public CasaAfiliada() {}

    // ── Constructor completo ───────────────────────────────────────────────────
    public CasaAfiliada(Integer idCasa, String direccion, String duiUsuario,
                        Integer numHabitaciones, Integer idDepto,
                        Integer idMunicipio, Integer idDistrito) {
        this.idCasa          = idCasa;
        this.direccion       = direccion;
        this.duiUsuario      = duiUsuario;
        this.numHabitaciones = numHabitaciones;
        this.idDepto         = idDepto;
        this.idMunicipio     = idMunicipio;
        this.idDistrito      = idDistrito;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Integer getIdCasa() { return idCasa; }
    public void setIdCasa(Integer idCasa) { this.idCasa = idCasa; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDuiUsuario() { return duiUsuario; }
    public void setDuiUsuario(String duiUsuario) { this.duiUsuario = duiUsuario; }

    public Integer getNumHabitaciones() { return numHabitaciones; }
    public void setNumHabitaciones(Integer numHabitaciones) { this.numHabitaciones = numHabitaciones; }

    public Integer getIdDepto() { return idDepto; }
    public void setIdDepto(Integer idDepto) { this.idDepto = idDepto; }

    public Integer getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(Integer idMunicipio) { this.idMunicipio = idMunicipio; }

    public Integer getIdDistrito() { return idDistrito; }
    public void setIdDistrito(Integer idDistrito) { this.idDistrito = idDistrito; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public boolean isActivo() { return this.deletedAt == null; }
}
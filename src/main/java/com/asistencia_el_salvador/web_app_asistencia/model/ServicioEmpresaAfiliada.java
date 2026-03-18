package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicio_empresaAfiliada")
public class ServicioEmpresaAfiliada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Column(name = "id_empresa", length = 45, nullable = false)
    private String idEmpresa;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "nombreServicio", length = 200)
    private String nombreServicio;

    @Column(name = "idPlan")
    private Integer idPlan;

    // Constructor vacío
    public ServicioEmpresaAfiliada() {
    }

    // Constructor con parámetros principales
    public ServicioEmpresaAfiliada(String idEmpresa, String nombreServicio, Integer idPlan, Integer estado) {
        this.idEmpresa = idEmpresa;
        this.nombreServicio = nombreServicio;
        this.idPlan = idPlan;
        this.estado = estado;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    // Método PrePersist para establecer la fecha de creación automáticamente
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "ServicioEmpresaAfiliada{" +
                "idServicio=" + idServicio +
                ", idEmpresa='" + idEmpresa + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", estado=" + estado +
                ", nombreServicio='" + nombreServicio + '\'' +
                ", idPlan=" + idPlan +
                '}';
    }
}

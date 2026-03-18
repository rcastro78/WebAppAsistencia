package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "servicio_plan_empresa")
@IdClass(ServicioPlanEmpresa.ServicioPlanEmpresaId.class)
public class ServicioPlanEmpresa {

    @Id
    @Column(name = "idServicio", nullable = false)
    private Integer idServicio;

    @Id
    @Column(name = "idEmpresa", nullable = false, length = 45)
    private String idEmpresa;

    @Id
    @Column(name = "idPlan", nullable = false)
    private Integer idPlan;

    @Column(name = "monto", precision = 9, scale = 2)
    private BigDecimal monto;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "estado")
    private Integer estado;

    public ServicioPlanEmpresa() {
        this.monto = BigDecimal.ZERO;
        this.estado = 1;
    }

    public ServicioPlanEmpresa(Integer idServicio, String idEmpresa, Integer idPlan) {
        this();
        this.idServicio = idServicio;
        this.idEmpresa = idEmpresa;
        this.idPlan = idPlan;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (monto == null) {
            monto = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = 1;
        }
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

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicioPlanEmpresa that = (ServicioPlanEmpresa) o;
        return Objects.equals(idServicio, that.idServicio) &&
                Objects.equals(idEmpresa, that.idEmpresa) &&
                Objects.equals(idPlan, that.idPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idServicio, idEmpresa, idPlan);
    }

    // Clase interna para la clave primaria compuesta
    public static class ServicioPlanEmpresaId implements Serializable {
        private Integer idServicio;
        private String idEmpresa;
        private Integer idPlan;

        public ServicioPlanEmpresaId() {
        }

        public ServicioPlanEmpresaId(Integer idServicio, String idEmpresa, Integer idPlan) {
            this.idServicio = idServicio;
            this.idEmpresa = idEmpresa;
            this.idPlan = idPlan;
        }

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

        public Integer getIdPlan() {
            return idPlan;
        }

        public void setIdPlan(Integer idPlan) {
            this.idPlan = idPlan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServicioPlanEmpresaId that = (ServicioPlanEmpresaId) o;
            return Objects.equals(idServicio, that.idServicio) &&
                    Objects.equals(idEmpresa, that.idEmpresa) &&
                    Objects.equals(idPlan, that.idPlan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idServicio, idEmpresa, idPlan);
        }
    }
}

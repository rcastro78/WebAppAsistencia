package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicio_plan_proveedor", schema = "asistenciaDB")
public class ServicioPlanProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idServicio")
    private Integer idServicio;

    @Column(name = "idProveedor")
    private Integer idProveedor;

    @Column(name = "idPlan")
    private Integer idPlan;

    @Column(name = "estado")
    private Integer estado = 1;

    @Column(name = "nombreServicio", length = 150)
    private String nombreServicio;

    @Column(name = "monto", precision = 9, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    public ServicioPlanProveedor() {
    }

    // Getters
    public Integer getIdServicio() {
        return idServicio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public Integer getEstado() {
        return estado;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (estado == null) {
            estado = 1;
        }
        if (monto == null) {
            monto = BigDecimal.ZERO;
        }
    }
}

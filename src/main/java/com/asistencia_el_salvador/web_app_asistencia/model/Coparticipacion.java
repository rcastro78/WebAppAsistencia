package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coparticipacion")
public class Coparticipacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idPlan")
    private Integer idPlan;

    @Column(name = "NITIntermediario", length = 45)
    private String nitIntermediario;

    @Column(name = "NITCoparticipe", length = 45)
    private String nitCoparticipe;

    @Column(name = "cobroAESAL", precision = 9, scale = 2)
    private BigDecimal cobroAESAL;

    @Column(name = "cobroIntermediario", precision = 9, scale = 2)
    private BigDecimal cobroIntermediario;

    @Column(name = "cobroCoparticipe", precision = 9, scale = 2)
    private BigDecimal cobroCoparticipe;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "estado")
    private Integer estado;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.estado = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdPlan() { return idPlan; }
    public void setIdPlan(Integer idPlan) { this.idPlan = idPlan; }

    public String getNitIntermediario() { return nitIntermediario; }
    public void setNitIntermediario(String nitIntermediario) { this.nitIntermediario = nitIntermediario; }

    public String getNitCoparticipe() { return nitCoparticipe; }
    public void setNitCoparticipe(String nitCoparticipe) { this.nitCoparticipe = nitCoparticipe; }

    public BigDecimal getCobroAESAL() { return cobroAESAL; }
    public void setCobroAESAL(BigDecimal cobroAESAL) { this.cobroAESAL = cobroAESAL; }

    public BigDecimal getCobroIntermediario() { return cobroIntermediario; }
    public void setCobroIntermediario(BigDecimal cobroIntermediario) { this.cobroIntermediario = cobroIntermediario; }

    public BigDecimal getCobroCoparticipe() { return cobroCoparticipe; }
    public void setCobroCoparticipe(BigDecimal cobroCoparticipe) { this.cobroCoparticipe = cobroCoparticipe; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}

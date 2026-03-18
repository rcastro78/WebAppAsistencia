package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_afiliado")
@IdClass(PlanAfiliadoID.class)
public class PlanAfiliado {

    @Id
    @Column(name = "DUI")
    private String dui;

    @Id
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "deletedAt")
    private String deletedAt;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "vigencia")
    private String vigencia;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String firma;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name="precio_plan_mensual")
    private Double precioPlanMensual=0.0;

    @Column(name="precio_plan_anual")
    private Double precioPlanAnual=0.0;



    // Constructor por defecto
    public PlanAfiliado() {}

    // Constructor con parámetros principales
    public PlanAfiliado(String dui, Integer idPlan, String vigencia, String firma, String observaciones,
                        double precioPlanMensual, double precioPlanAnual) {
        this.dui = dui;
        this.idPlan = idPlan;
        this.vigencia = vigencia;
        this.firma = firma;
        this.observaciones = observaciones;
        this.estado = 1; // activo por defecto
        this.precioPlanMensual = precioPlanMensual;
        this.precioPlanAnual = precioPlanAnual;

    }

    // Getters y setters
    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getPrecioPlanMensual() {
        return precioPlanMensual;
    }

    public void setPrecioPlanMensual(Double precioPlanMensual) {
        this.precioPlanMensual = precioPlanMensual;
    }

    public Double getPrecioPlanAnual() {
        return precioPlanAnual;
    }

    public void setPrecioPlanAnual(Double precioPlanAnual) {
        this.precioPlanAnual = precioPlanAnual;
    }

    @Override
    public String toString() {
        return "PlanAfiliado{" +
                "dui='" + dui + '\'' +
                ", idPlan=" + idPlan +
                ", estado=" + estado +
                ", vigencia='" + vigencia + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
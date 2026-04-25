package com.asistencia_el_salvador.web_app_asistencia.dto;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliado;

import java.time.LocalDateTime;

public class PlanAfiliadoDTO {

    private String dui;
    private Integer idPlan;
    private String nombrePlan;   // ← el campo nuevo
    private Integer estado;
    private String vigencia;
    private Double precioPlanMensual;
    private Double precioPlanAnual;
    private String observaciones;
    private LocalDateTime createdAt;

    // Constructor
    public PlanAfiliadoDTO(PlanAfiliado p, String nombrePlan) {
        this.dui              = p.getDui();
        this.idPlan           = p.getIdPlan();
        this.nombrePlan       = nombrePlan;
        this.estado           = p.getEstado();
        this.vigencia         = p.getVigencia();
        this.precioPlanMensual= p.getPrecioPlanMensual();
        this.precioPlanAnual  = p.getPrecioPlanAnual();
        this.observaciones    = p.getObservaciones();
        this.createdAt        = p.getCreatedAt();
    }

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

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

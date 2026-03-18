package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vwplanescobertura")
public class PlanesCobertura {
    @Id
    @Column(name = "id_cobertura")
    private int idCobertura;

    @Column(name = "id_plan")
    private int idPlan;

    @Column(name = "nombreCobertura")
    private String nombreCobertura;

    @Column(name = "nombrePlan")
    private String nombrePlan;

    @Column(name = "costoPlan")
    private Double costoPlan;

    @Column(name = "costoPlanAnual")
    private Double costoPlanAnual;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "idPais")
    private int idPais;

    public int getEventos() {
        return eventos;
    }

    public void setEventos(int eventos) {
        this.eventos = eventos;
    }

    @Column(name = "eventos")
    private int eventos;

    public int getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(int idCobertura) {
        this.idCobertura = idCobertura;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public void setNombreCobertura(String nombreCobertura) {
        this.nombreCobertura = nombreCobertura;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public Double getCostoPlan() {
        return costoPlan;
    }

    public void setCostoPlan(Double costoPlan) {
        this.costoPlan = costoPlan;
    }

    public Double getCostoPlanAnual() {
        return costoPlanAnual;
    }

    public void setCostoPlanAnual(Double costoPlanAnual) {
        this.costoPlanAnual = costoPlanAnual;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }
}

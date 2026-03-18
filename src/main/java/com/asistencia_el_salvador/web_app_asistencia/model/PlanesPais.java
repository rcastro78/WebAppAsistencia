package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vw_planes_pais")
public class PlanesPais {
    @Id
    @Column(name = "id_plan")
    private Integer idPlan;
    @Column(name = "nombrePlan", length = 45)
    private String nombrePlan;
    @Column(name = "costo_plan")
    private Double costoPlan;
    @Column(name = "id_pais")
    private Integer idPais;
    @Column(name = "nombrePais", length = 45)
    private String nombrePais;
    @Column(name = "moneda", length = 3)
    private String moneda;
    @Column(name = "nombre", length = 20)
    private String nombre;

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

    public Double getCostoPlan() {
        return costoPlan;
    }

    public void setCostoPlan(Double costoPlan) {
        this.costoPlan = costoPlan;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

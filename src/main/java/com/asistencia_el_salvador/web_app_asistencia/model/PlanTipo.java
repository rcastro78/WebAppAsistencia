package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan_tipo")
public class PlanTipo {
    @Id
    @Column(name = "idPlanTipo")
    private Integer idPlanTipo;
    @Column(name = "nombreTipo", length = 45)
    private String nombreTipo;
    @Column(name = "estado")
    private int estado;

    public Integer getIdPlanTipo() {
        return idPlanTipo;
    }

    public void setIdPlanTipo(Integer idPlanTipo) {
        this.idPlanTipo = idPlanTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}

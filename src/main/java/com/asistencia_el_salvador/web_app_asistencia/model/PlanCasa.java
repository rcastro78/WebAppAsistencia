package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_casa")
public class PlanCasa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPlanCasa;
    @Column(name = "NIC", length = 45)
    private String NIC;
    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "estado")
    private int estado;

    public long getIdPlanCasa() {
        return idPlanCasa;
    }

    public void setIdPlanCasa(long idPlanCasa) {
        this.idPlanCasa = idPlanCasa;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}

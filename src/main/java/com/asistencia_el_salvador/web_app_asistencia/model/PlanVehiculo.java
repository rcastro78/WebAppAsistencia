package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_vehiculo")
public class PlanVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPlanVehiculo;
    @Column(name = "placaVehiculo", length = 45)
    private String placaVehiculo;
    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "estado")
    private int estado;

    public long getIdPlanVehiculo() {
        return idPlanVehiculo;
    }

    public void setIdPlanVehiculo(long idPlanVehiculo) {
        this.idPlanVehiculo = idPlanVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
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

package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity(name = "vw_total_afiliados_por_plan")
public class TotalesAfiliadosPlan {
    @Id
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "nombrePlan", length = 200)
    private String nombrePlan;

    @Column(name = "totalAfiliados")
    private Long totalAfiliados;

    @Column(name = "ejecutivo", length = 200)
    private String ejecutivo;

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

    public Long getTotalAfiliados() {
        return totalAfiliados;
    }

    public void setTotalAfiliados(Long totalAfiliados) {
        this.totalAfiliados = totalAfiliados;
    }

    public String getEjecutivo() {
        return ejecutivo;
    }

    public void setEjecutivo(String ejecutivo) {
        this.ejecutivo = ejecutivo;
    }

    @Override
    public String toString() {
        return "TotalAfiliadosPorPlan{" +
                "idPlan=" + idPlan +
                ", nombrePlan='" + nombrePlan + '\'' +
                ", totalAfiliados=" + totalAfiliados +
                '}';
    }
}

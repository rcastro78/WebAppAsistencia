package com.asistencia_el_salvador.web_app_asistencia.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase que representa la clave compuesta de PlanAfiliado
 * Debe implementar Serializable y tener equals() y hashCode()
 */
public class PlanAfiliadoID implements Serializable {

    private String dui;
    private Integer idPlan;

    // Constructor por defecto (requerido)
    public PlanAfiliadoID() {}

    // Constructor con parámetros
    public PlanAfiliadoID(String dui, Integer idPlan) {
        this.dui = dui;
        this.idPlan = idPlan;
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

    // equals() y hashCode() son OBLIGATORIOS para IdClass
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanAfiliadoID that = (PlanAfiliadoID) o;
        return Objects.equals(dui, that.dui) && Objects.equals(idPlan, that.idPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dui, idPlan);
    }

    @Override
    public String toString() {
        return "PlanAfiliadoId{" +
                "dui='" + dui + '\'' +
                ", idPlan=" + idPlan +
                '}';
    }
}

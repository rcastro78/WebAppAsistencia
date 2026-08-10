package com.asistencia_el_salvador.web_app_asistencia.dto;

public class ResumenPlanesCorpDTO {
    private int cantidad;
    private String nombrePlan;
    public ResumenPlanesCorpDTO(String nombrePlan, int cantidad) {
        this.cantidad = cantidad;
        this.nombrePlan = nombrePlan;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public interface ResumenPlanesCorpProjection {
        String getNombrePlan();
        Long getCantidad();
    }
}



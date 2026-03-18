package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;

@Entity
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private int idPlan;
    @Column(name = "nombrePlan", length = 45)
    private String nombrePlan;
    @Column(name = "costoPlan")
    private Double costoPlan;
    @Column(name = "costoPlanAnual")
    private Double costoPlanAnual;
    @Column(name = "estado")
    private int estado = 1; // default 1 (activo)
    @Column(name = "linkPago")
    private String linkPago;

    @Column(name = "idPais")
    private int idPais;
    @Column(name = "moneda", length = 3)
    private String moneda;

    @Column(name = "linkPagoAnual")
    private String linkPagoAnual;


    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
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

    public Double getCostoPlanAnual() {
        return costoPlanAnual;
    }

    public void setCostoPlanAnual(Double costoPlanAnual) {
        this.costoPlanAnual = costoPlanAnual;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getLinkPago() {
        return linkPago;
    }

    public void setLinkPago(String linkPago) {
        this.linkPago = linkPago;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }


    public String getLinkPagoAnual() {
        return linkPagoAnual;
    }

    public void setLinkPagoAnual(String linkPagoAnual) {
        this.linkPagoAnual = linkPagoAnual;
    }

    public String getSimboloMoneda() {
        if (this.moneda == null) return "$";

        switch (this.moneda) {
            case "HNL": return "L";
            case "USD": return "$";
            case "GTQ": return "Q";
            case "CRC": return "C";
            case "MXN": return "$";
            case "NIO": return "C$";
            default: return "$";
        }
    }
}

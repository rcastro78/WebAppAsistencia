package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity(name = "vw_proveedor_plan_cobertura")
public class ProveedorPlanCobertura {
    @Column(name = "idProveedor")
    private Integer idProveedor;
    @Column(name = "nombreProveedor", length = 100)
    private String nombreProveedor;
    @Column(name = "imagenURL", length = 100)
    private String imagenURL;
    @Id
    @Column(name = "idCobertura")
    private Integer idCobertura;
    @Column(name = "nombreCobertura", length = 100)
    private String nombreCobertura;
    @Column(name = "tarifa")
    private Double tarifa;
    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "nombrePlan", length = 100)
    private String nombrePlan;
    @Column(name = "estado")
    private Integer estado;

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public Integer getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(Integer idCobertura) {
        this.idCobertura = idCobertura;
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public void setNombreCobertura(String nombreCobertura) {
        this.nombreCobertura = nombreCobertura;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
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
}

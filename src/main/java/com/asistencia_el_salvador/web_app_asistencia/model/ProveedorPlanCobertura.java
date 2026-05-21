package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity(name = "vw_proveedor_plan_cobertura")
@IdClass(ProveedorPlanCobertura.PK.class)
public class ProveedorPlanCobertura {
    @Id
    @Column(name = "idProveedor")
    private Integer idProveedor;
    @Id
    @Column(name = "idCobertura")
    private Integer idCobertura;
    @Id
    @Column(name = "idPlan")
    private Integer idPlan;
    @Id
    @Column(name = "idServicioProveedorCobertura")
    private Integer idServicioProveedorCobertura;


    @Column(name = "nombreProveedor", length = 100)
    private String nombreProveedor;
    @Column(name = "imagenURL", length = 100)
    private String imagenURL;

    @Column(name = "nombreCobertura", length = 100)
    private String nombreCobertura;
    @Column(name = "tarifa")
    private Double tarifa;

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

    public static class PK implements Serializable {
        private Integer idProveedor;
        private Integer idCobertura;
        private Integer idPlan;
        private Integer idServicioProveedorCobertura;

        public PK() {}
        public PK(Integer idProveedor, Integer idCobertura, Integer idPlan, Integer idServicioProveedorCobertura) {
            this.idProveedor             = idProveedor;
            this.idCobertura             = idCobertura;
            this.idPlan                  = idPlan;
            this.idServicioProveedorCobertura = idServicioProveedorCobertura;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PK pk)) return false;
            return Objects.equals(idProveedor,             pk.idProveedor)
                    && Objects.equals(idCobertura,             pk.idCobertura)
                    && Objects.equals(idPlan,                  pk.idPlan)
                    && Objects.equals(idServicioProveedorCobertura, pk.idServicioProveedorCobertura);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idProveedor, idCobertura, idPlan, idServicioProveedorCobertura);
        }
    }

    // Getter/Setter nuevo
    public Integer getIdServicioProveedorCobertura()        { return idServicioProveedorCobertura; }
    public void setIdServicioProveedorCobertura(Integer v)  { this.idServicioProveedorCobertura = v; }
}





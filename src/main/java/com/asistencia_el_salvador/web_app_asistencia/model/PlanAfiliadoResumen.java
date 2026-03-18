package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(
        name = "vwplanafiliado"
)
public class PlanAfiliadoResumen implements Serializable {
    @Id
    @Column(name = "DUI", length = 10, nullable = false, unique = true)
    private String dui;

    @Column(name = "afiliadoNombre", length = 90)
    private String nombre;

    @Column(name = "vigencia", length = 45)
    private String vigencia;

    @Column(name = "nombrePlan", length = 45)
    private String nombrePlan;

    @Column(name = "numTarjeta", length = 45)
    private String numTarjeta;

    @Column(name = "precio_plan_mensual")
    private Double precioPlanMensual;

    @Column(name = "precio_plan_anual")
    private Double precioPlanAnual;

    @Column(name = "ultimoMesPagado")
    private String ultimoMesPagado;

    @Column(name = "ultimoAnioPagado")
    private String ultimoAnioPagado;

    @Column(name = "ultimoPeriodoPagado")
    private String ultimoPeriodoPagado;

    @Column(name = "carnetActivo")
    private int carnetActivo;

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public Double getPrecioPlanMensual() {
        return precioPlanMensual;
    }

    public void setPrecioPlanMensual(Double precioPlanMensual) {
        this.precioPlanMensual = precioPlanMensual;
    }

    public Double getPrecioPlanAnual() {
        return precioPlanAnual;
    }

    public void setPrecioPlanAnual(Double precioPlanAnual) {
       this.precioPlanAnual = precioPlanAnual;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public String getUltimoMesPagado() {
        return ultimoMesPagado;
    }

    public void setUltimoMesPagado(String ultimoMesPagado) {
        this.ultimoMesPagado = ultimoMesPagado;
    }

    public String getUltimoAnioPagado() {
        return ultimoAnioPagado;
    }

    public void setUltimoAnioPagado(String ultimoAnioPagado) {
        this.ultimoAnioPagado = ultimoAnioPagado;
    }

    public String getUltimoPeriodoPagado() {
        return ultimoPeriodoPagado;
    }

    public void setUltimoPeriodoPagado(String ultimoPeriodoPagado) {
        this.ultimoPeriodoPagado = ultimoPeriodoPagado;
    }

    public int getCarnetActivo() {
        return carnetActivo;
    }

    public boolean tieneCarnetActivo() {
        if(carnetActivo==0)
                return false;
            else
                return true;
    }

    public void setCarnetActivo(int carnetActivo) {
        this.carnetActivo = carnetActivo;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_pago_afiliado")
@IdClass(PagoAfiliadoPK.class)
public class PagoAfiliado {

    @Id
    @Column(name = "dui", length = 10, nullable = false)
    private String dui;

    @Id
    @Column(name = "mes")
    private int mes;

    @Id
    @Column(name = "anio")
    private int anio;

    @Column(name = "cantidadPagada")
    private double cantidadPagada;

    @Column(name = "formaPagoNombre")
    private String formaPagoNombre;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "nombrePlan")
    private String nombrePlan;

    @Column(name = "ejecutivoAsignado")
    private String ejecutivoAsignado;

    @Column(name = "voucherURL", length = 200)
    private String voucherURL;

    @Column(name = "nombreCompleto")
    private String nombreCompleto;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "nombrePais")
    private String nombrePais;

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getCantidadPagada() {
        return cantidadPagada;
    }

    public void setCantidadPagada(double cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
    }

    public String getFormaPagoNombre() {
        return formaPagoNombre;
    }

    public void setFormaPagoNombre(String formaPagoNombre) {
        this.formaPagoNombre = formaPagoNombre;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public String getVoucherURL() {
        return voucherURL;
    }

    public void setVoucherURL(String voucherURL) {
        this.voucherURL = voucherURL;
    }

    public String getEjecutivoAsignado() {
        return ejecutivoAsignado;
    }

    public void setEjecutivoAsignado(String ejecutivoAsignado) {
        this.ejecutivoAsignado = ejecutivoAsignado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }
}

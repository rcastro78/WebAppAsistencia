package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "v_estado_pagos_afiliados")
public class AfiliadoPagoEstado {
    @Id
    @Column(name = "dui", length = 10, nullable = false, unique = true)
    private String dui;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "estadoContrato")
    private Integer estadoContrato;

    @Column(name = "nombrePlan")
    private String nombrePlan;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "ultimoMesPagado")
    private Integer ultimoMesPagado;

    @Column(name = "ultimoAnioPagado")
    private Integer ultimoAnioPagado;

    @Column(name = "ultimoPagoMonto")
    private Double ultimoPagoMonto;

    @Column(name = "fechaUltimoPago")
    private LocalDate ultimaFechaPago;

    @Column(name = "estadoPago")
    private String estadoPago;


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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(Integer estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Integer getUltimoMesPagado() {
        return ultimoMesPagado;
    }

    public void setUltimoMesPagado(Integer ultimoMesPagado) {
        this.ultimoMesPagado = ultimoMesPagado;
    }

    public Integer getUltimoAnioPagado() {
        return ultimoAnioPagado;
    }

    public void setUltimoAnioPagado(Integer ultimoAnioPagado) {
        this.ultimoAnioPagado = ultimoAnioPagado;
    }

    public Double getUltimoPagoMonto() {
        return ultimoPagoMonto;
    }

    public void setUltimoPagoMonto(Double ultimoPagoMonto) {
        this.ultimoPagoMonto = ultimoPagoMonto;
    }

    public LocalDate getUltimaFechaPago() {
        return ultimaFechaPago;
    }

    public void setUltimaFechaPago(LocalDate ultimaFechaPago) {
        this.ultimaFechaPago = ultimaFechaPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}

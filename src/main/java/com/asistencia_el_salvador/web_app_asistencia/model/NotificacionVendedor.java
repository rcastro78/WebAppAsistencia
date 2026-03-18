package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_notificaciones_pagos_vendedor")
public class NotificacionVendedor {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "dui", length = 10)
    private String dui;
    @Column(name = "textoNotificacion", length = 100)
    private String textoNotificacion;
    @Column(name = "tipo", length = 45)
    private String tipo;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    @Column(name = "afiliadoNombre", length = 100)
    private String afiliadoNombre;
    @Column(name = "ejecutivoAsignado", length = 100)
    private String ejecutivoAsignado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTextoNotificacion() {
        return textoNotificacion;
    }

    public void setTextoNotificacion(String textoNotificacion) {
        this.textoNotificacion = textoNotificacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAfiliadoNombre() {
        return afiliadoNombre;
    }

    public void setAfiliadoNombre(String afiliadoNombre) {
        this.afiliadoNombre = afiliadoNombre;
    }

    public String getEjecutivoAsignado() {
        return ejecutivoAsignado;
    }

    public void setEjecutivoAsignado(String ejecutivoAsignado) {
        this.ejecutivoAsignado = ejecutivoAsignado;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="contrato")
public class Contrato implements Serializable {
    @Id
    @Column(name = "DUI", length = 20, nullable = false, unique = true)
    private String dui;
    @Column(name = "textoHTMLContrato")
    private String textoHTMLContrato;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    @Column(name = "createdBy")
    private String createdBy;
    @Column(name = "firmado")
    private Integer firmado;
    @Column(name = "estadoContrato")
    private Integer estadoContrato;

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTextoHTMLContrato() {
        return textoHTMLContrato;
    }

    public void setTextoHTMLContrato(String textoHTMLContrato) {
        this.textoHTMLContrato = textoHTMLContrato;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getFirmado() {
        return firmado;
    }

    public void setFirmado(Integer firmado) {
        this.firmado = firmado;
    }

    public Integer getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(Integer estadoContrato) {
        this.estadoContrato = estadoContrato;
    }
}

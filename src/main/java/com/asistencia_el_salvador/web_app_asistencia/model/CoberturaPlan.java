package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
@Entity
@Table(name = "cobertura_plan")
public class CoberturaPlan {
    @Id
    @Column(name = "idCobertura")
    private Integer idCobertura;
    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "estado")
    private Integer estado = 1; // default 1 (activo)
    @Column(name = "updatedAt", updatable = true, insertable = false)
    private LocalDateTime updatedAt;
    @Column(name = "eventos")
    private int eventos;

    public int getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(int idCobertura) {
        this.idCobertura = idCobertura;
    }


    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getEventos() {
        return eventos;
    }

    public void setEventos(int eventos) {
        this.eventos = eventos;
    }

}

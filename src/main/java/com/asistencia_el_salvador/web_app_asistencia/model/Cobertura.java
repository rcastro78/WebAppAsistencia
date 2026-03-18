package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity(name = "cobertura")
public class Cobertura {
    @Id
    @Column(name = "id_cobertura")
    private int idCobertura;
    @Column(name = "nombreCobertura")
    private String nombreCobertura;
    @Column(name = "estado")
    private int estado;
    @Column(name = "updatedAt", insertable = true)
    private LocalDateTime updatedAt;
    @Column(name = "eventos")
    private int eventos;
    @Column(name = "limiteEconomico")
    private double limiteEconomico;
    @Column(name = "porAnio")
    private int porAnio;

    public int getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(int idCobertura) {
        this.idCobertura = idCobertura;
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public void setNombreCobertura(String nombreCobertura) {
        this.nombreCobertura = nombreCobertura;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
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

    public double getLimiteEconomico() {
        return limiteEconomico;
    }

    public void setLimiteEconomico(double limiteEconomico) {
        this.limiteEconomico = limiteEconomico;
    }

    public int getPorAnio() {
        return porAnio;
    }

    public void setPorAnio(int porAnio) {
        this.porAnio = porAnio;
    }
}

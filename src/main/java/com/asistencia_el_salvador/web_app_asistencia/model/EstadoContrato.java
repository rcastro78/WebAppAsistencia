package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_contrato")
public class EstadoContrato {
    @Id
    @Column(name = "idEstadoContrato", unique = true, insertable = false)
    private int idEstadoContrato;
    @Column(name = "nombreEstado")
    private String nombreEstado;

    public int getIdEstadoContrato() {
        return idEstadoContrato;
    }

    public void setIdEstadoContrato(int idEstadoContrato) {
        this.idEstadoContrato = idEstadoContrato;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
}

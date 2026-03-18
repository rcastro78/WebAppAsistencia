package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_civil")
public class EstadoCivil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEstadoCivil")
    private Integer idEstadoCivil;

    @Column(name = "nombreEstado", length = 45)
    private String nombreEstado;

    public Integer getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public void setIdEstadoCivil(Integer idEstadoCivil) {
        this.idEstadoCivil = idEstadoCivil;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
}

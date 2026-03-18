package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "med_tipoConsulta")
public class MedTipoConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoConsulta")
    private Integer idTipoConsulta;

    @Column(name = "tipoConsulta", length = 45)
    private String tipoConsulta;

    public Integer getIdTipoConsulta() {
        return idTipoConsulta;
    }

    public void setIdTipoConsulta(Integer idTipoConsulta) {
        this.idTipoConsulta = idTipoConsulta;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }
}

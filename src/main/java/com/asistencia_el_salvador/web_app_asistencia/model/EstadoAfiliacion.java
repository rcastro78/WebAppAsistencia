package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_afiliacion")
public class EstadoAfiliacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEstadoAfiliado")
    private Integer idEstadoAfiliado;
    @Column(name = "estadoNombre")
    private String estadoNombre;

    public Integer getIdEstadoAfiliado() {
        return idEstadoAfiliado;
    }

    public void setIdEstadoAfiliado(Integer idEstadoAfiliado) {
        this.idEstadoAfiliado = idEstadoAfiliado;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }
}

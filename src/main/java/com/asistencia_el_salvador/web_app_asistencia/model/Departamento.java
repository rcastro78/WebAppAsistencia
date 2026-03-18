package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departamento")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_depto")
    private Integer idDepto;

    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "nombreDepartamento")
    private String nombreDepartamento;

    // Constructor vacío
    public Departamento() {}

    // Constructor con parámetros
    public Departamento(Integer idDepto, Integer idPais, String nombreDepartamento) {
        this.idDepto = idDepto;
        this.idPais = idPais;
        this.nombreDepartamento = nombreDepartamento;
    }

    // Getters y Setters
    public Integer getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(Integer idDepto) {
        this.idDepto = idDepto;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
}

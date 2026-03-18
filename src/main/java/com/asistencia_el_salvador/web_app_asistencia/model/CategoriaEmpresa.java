package com.asistencia_el_salvador.web_app_asistencia.model;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "categoria_empresa")
public class CategoriaEmpresa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategoria")
    private Integer idCategoria;
    @Column(name = "catNombre")
    private String catNombre;

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCatNombre() {
        return catNombre;
    }

    public void setCatNombre(String catNombre) {
        this.catNombre = catNombre;
    }
}

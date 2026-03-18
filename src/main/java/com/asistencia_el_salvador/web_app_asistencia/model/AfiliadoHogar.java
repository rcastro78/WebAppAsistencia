package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "afiliado_hogar")
public class AfiliadoHogar {
    @Id
    @Column(name = "duiAfiliado", length = 10, nullable = false, unique = true)
    private String duiAfiliado;
    @Column(name = "direccion", length = 100)
    private String direccion;
    @Column(name = "idPais")
    private Integer idPais;
    @Column(name = "idMunicipio")
    private Integer idMunicipio;
    @Column(name = "idDepto")
    private Integer idDepto;

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public Integer getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(Integer idDepto) {
        this.idDepto = idDepto;
    }
}

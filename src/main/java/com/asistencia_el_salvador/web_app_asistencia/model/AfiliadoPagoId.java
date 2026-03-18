package com.asistencia_el_salvador.web_app_asistencia.model;

import java.io.Serializable;
import java.util.Objects;

public class AfiliadoPagoId implements Serializable {

    private String duiAfiliado;
    private Integer mes;
    private String anio;

    public AfiliadoPagoId() {
    }

    public AfiliadoPagoId(String duiAfiliado, Integer mes, String anio) {
        this.duiAfiliado = duiAfiliado;
        this.mes = mes;
        this.anio = anio;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AfiliadoPagoId that = (AfiliadoPagoId) o;
        return Objects.equals(duiAfiliado, that.duiAfiliado) &&
                Objects.equals(mes, that.mes) &&
                Objects.equals(anio, that.anio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duiAfiliado, mes, anio);
    }
}


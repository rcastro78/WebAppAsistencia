package com.asistencia_el_salvador.web_app_asistencia.model;

import java.io.Serializable;
import java.util.Objects;

public class PagoAfiliadoPK implements Serializable {
    private String dui;
    private int mes;
    private int anio;

    public PagoAfiliadoPK() {}

    public PagoAfiliadoPK(String dui, int mes, int anio) {
        this.dui = dui;
        this.mes = mes;
        this.anio = anio;
    }

    // getters y setters

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoAfiliadoPK that = (PagoAfiliadoPK) o;
        return mes == that.mes &&
                anio == that.anio &&
                Objects.equals(dui, that.dui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dui, mes, anio);
    }
}

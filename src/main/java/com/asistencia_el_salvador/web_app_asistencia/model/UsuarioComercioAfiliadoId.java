package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioComercioAfiliadoId implements Serializable {
    private String nit;
    private String dui;

    public UsuarioComercioAfiliadoId() {
    }

    public UsuarioComercioAfiliadoId(String nit, String dui) {
        this.nit = nit;
        this.dui = dui;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioComercioAfiliadoId that = (UsuarioComercioAfiliadoId) o;
        return Objects.equals(nit, that.nit) && Objects.equals(dui, that.dui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nit, dui);
    }
}

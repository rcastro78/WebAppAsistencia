package com.asistencia_el_salvador.web_app_asistencia.model;

import java.io.Serializable;

public class UsuarioComercioPK implements Serializable {
    private String nit;
    private String emailAsociado;

    public UsuarioComercioPK() {
    }

    public UsuarioComercioPK(String nit, String emailAsociado) {
        this.nit = nit;
        this.emailAsociado = emailAsociado;
    }

    public String getNIT() {
        return nit;
    }

    public void setNIT(String nit) {
        this.nit = nit;
    }

    public String getEmailAsociado() {
        return emailAsociado;
    }

    public void setEmailAsociado(String emailAsociado) {
        this.emailAsociado = emailAsociado;
    }
}

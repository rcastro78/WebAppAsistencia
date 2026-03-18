package com.asistencia_el_salvador.web_app_asistencia.model;

import java.util.ArrayList;
import java.util.List;

public class CargaMasivaResultado {
    private int totalProcesados;
    private int exitosos;
    private int errores;
    private List<String> mensajesError;

    // Constructores, getters y setters
    public CargaMasivaResultado() {
        this.mensajesError = new ArrayList<>();
    }

    public int getTotalProcesados() {
        return totalProcesados;
    }

    public void setTotalProcesados(int totalProcesados) {
        this.totalProcesados = totalProcesados;
    }

    public int getExitosos() {
        return exitosos;
    }

    public void setExitosos(int exitosos) {
        this.exitosos = exitosos;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public List<String> getMensajesError() {
        return mensajesError;
    }

    public void setMensajesError(List<String> mensajesError) {
        this.mensajesError = mensajesError;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "info_empleo_afiliado")
public class InfoEmpleoAfiliado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEmpleoAfiliado")
    private Integer idEmpleoAfiliado;

    @Column(name = "duiAfiliado", length = 45)
    private String duiAfiliado;

    @Column(name = "profesion", length = 100)
    private String profesion;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "jefeInmediato", length = 100)
    private String jefeInmediato;

    @Column(name = "fechaInicio")
    private LocalDate fechaInicio;

    @Column(name = "fechaFin")
    private LocalDate fechaFin;

    @Column(name = "estado")
    private int estado;

    @Column(name = "actual")
    private int actual;

    public InfoEmpleoAfiliado() {
    }

    public Integer getIdEmpleoAfiliado() {
        return idEmpleoAfiliado;
    }

    public void setIdEmpleoAfiliado(Integer idEmpleoAfiliado) {
        this.idEmpleoAfiliado = idEmpleoAfiliado;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getJefeInmediato() {
        return jefeInmediato;
    }

    public void setJefeInmediato(String jefeInmediato) {
        this.jefeInmediato = jefeInmediato;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }
}

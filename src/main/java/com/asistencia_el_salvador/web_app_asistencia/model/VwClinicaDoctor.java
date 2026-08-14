package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "vw_clinica_doctor")
public class VwClinicaDoctor {
    @Id
    @Column(name = "idClinica")
    private Integer idClinica;
    @Column(name = "doctor")
    private String doctor;
    @Column(name = "dui")
    private String dui;
    @Column(name = "nombreClinica")
    private String nombreClinica;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "nombreDepartamento")
    private String nombreDepartamento;
    @Column(name = "nombreMunicipio")
    private String nombreMunicipio;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "email")
    private String email;
    @Column(name = "doctorEstado")
    private Integer doctorEstado;
    @Column(name = "clinicaEstado")
    private Integer clinicaEstado;

    public Integer getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Integer idClinica) {
        this.idClinica = idClinica;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombreClinica() {
        return nombreClinica;
    }

    public void setNombreClinica(String nombreClinica) {
        this.nombreClinica = nombreClinica;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDoctorEstado() {
        return doctorEstado;
    }

    public void setDoctorEstado(Integer doctorEstado) {
        this.doctorEstado = doctorEstado;
    }

    public Integer getClinicaEstado() {
        return clinicaEstado;
    }

    public void setClinicaEstado(Integer clinicaEstado) {
        this.clinicaEstado = clinicaEstado;
    }
}

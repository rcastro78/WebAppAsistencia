package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import jakarta.persistence.*;

@Entity
@Table(name = "institucion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "nombreInstitucion")
        })
public class Institucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, unique = true)
    private String nombreInstitucion;

    @Column(length = 14)
    private String telefono;

    @Column
    private Integer idPais;

    @Column(length = 100)
    private String direccion;

    @Column(length = 200)
    private String observaciones;

    @Column(length = 45)
    private String email;

    @Column(nullable = false)
    private Boolean estado = false;

    // ====== Getters y Setters ======
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
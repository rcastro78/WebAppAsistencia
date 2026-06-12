package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresa_sucursal")
public class EmpresaSucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa_sucursal")
    private Integer idEmpresaSucursal;
    @Column(name = "NIT", length = 45, nullable = false)
    private String nit;
    @Column(name = "contacto", length = 100)
    private String contacto;
    @Column(name = "telefono", length = 100)
    private String telefono;
    @Column(name = "direccion", length = 100)
    private String direccion;
    @Column(name = "email", length = 100)
    private String email;
    @Column(name = "latitud")
    private Double latitud;
    @Column(name = "longitud")
    private Double longitud;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    @Column(name = "estado")
    private Integer estado;


    public Integer getIdEmpresaSucursal() {
        return idEmpresaSucursal;
    }

    public void setIdEmpresaSucursal(Integer idEmpresaSucursal) {
        this.idEmpresaSucursal = idEmpresaSucursal;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}

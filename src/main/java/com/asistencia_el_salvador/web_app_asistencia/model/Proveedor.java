package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProveedor")
    private Integer idProveedor;

    @Column(name = "NIT", length = 45, nullable = false)
    private String nit;

    @Column(name = "nombreProveedor", length = 100)
    private String nombreProveedor;

    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "estado")
    private Boolean estado = true;

    @Column(name = "direccion", length = 45)
    private String direccion;

    @Column(name = "telefono", length = 45)
    private String telefono;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "repreLegalNombre", length = 100)
    private String repreLegalNombre;

    @Column(name = "imagenURL", length = 100)
    private String imagenURL;

    @Column(name = "idCategoriaEmpresa")
    private Integer idCategoriaEmpresa;

    @Column(name = "idPais")
    private Integer idPais;

    // ====== Getters & Setters ======

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public Integer getIdCategoriaEmpresa() {
        return idCategoriaEmpresa;
    }

    public void setIdCategoriaEmpresa(Integer idCategoriaEmpresa) {
        this.idCategoriaEmpresa = idCategoriaEmpresa;
    }

    public String getRepreLegalNombre() {
        return repreLegalNombre;
    }

    public void setRepreLegalNombre(String repreLegalNombre) {
        this.repreLegalNombre = repreLegalNombre;
    }
}


package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "empresaAfiliada")
public class EmpresaAfiliada implements Serializable {

    @Id
    @Column(name = "NIT", length = 45, nullable = false)
    private String nit;

    @Column(name = "nombreEmpresa", length = 45, unique = true)
    private String nombreEmpresa;

    @Column(name = "direccion", length = 45)
    private String direccion;

    @Column(name = "telefono", length = 45)
    private String telefono;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "repreLegalNombre", length = 45)
    private String repreLegalNombre;

    @Column(name = "imagenURL", length = 100)
    private String imagenURL;

    @Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "idCategoriaEmpresa")
    private Integer idCategoriaEmpresa;

    @Column(name = "idPais")
    private Integer idPais;

    @Column(name = "rubro")
    private Integer rubro;

    // ====== Getters y Setters ======
    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
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

    public String getRepreLegalNombre() {
        return repreLegalNombre;
    }

    public void setRepreLegalNombre(String repreLegalNombre) {
        this.repreLegalNombre = repreLegalNombre;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getIdCategoriaEmpresa() {
        return idCategoriaEmpresa;
    }

    public void setIdCategoriaEmpresa(Integer idCategoriaEmpresa) {
        this.idCategoriaEmpresa = idCategoriaEmpresa;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public Integer getRubro() {
        return rubro;
    }

    public void setRubro(Integer rubro) {
        this.rubro = rubro;
    }
}


package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vwSucursalesProveedor")
public class VWSucursalesProveedor {
    @Id
    @Column(name = "idProveedor")
    private int idProveedor;
    @Column(name = "NIT")
    private String NIT;
    @Column(name = "nombreProveedor")
    private String nombreProveedor;
    @Column(name = "imagenURL")
    private String imagenURL;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "telCasaMatriz")
    private String telCasaMatriz;
    @Column(name = "contacto")
    private String contacto;
    @Column(name = "latitud")
    private Double latitud;
    @Column(name = "longitud")
    private Double longitud;
    @Column(name = "catNombre")
    private String catNombre;

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNit() {
        return NIT;
    }

    public void setNit(String nit) {
        this.NIT = nit;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelCasaMatriz() {
        return telCasaMatriz;
    }

    public void setTelCasaMatriz(String telCasaMatriz) {
        this.telCasaMatriz = telCasaMatriz;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    public String getCatNombre() {
        return catNombre;
    }

    public void setCatNombre(String catNombre) {
        this.catNombre = catNombre;
    }
}

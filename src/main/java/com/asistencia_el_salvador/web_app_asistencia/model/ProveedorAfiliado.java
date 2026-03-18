package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vw_proveedor_afiliado")
public class ProveedorAfiliado {

    @Id
    @Column(name = "idProveedor")
    private int idProveedor;

    @Column(name = "nit")
    private String nit;

    @Column(name = "nombreProveedor")
    private String nombreProveedor;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "imagenURL")
    private String imagenURL;

    @Column(name = "catNombre")
    private String catNombre;

    @Column(name = "nombrePais")
    private String nombrePais;

    @Column(name = "idCategoriaEmpresa")
    private int idCategoriaEmpresa;

    @Column(name = "estado")
    private int estado;

    // Constructores
    public ProveedorAfiliado() {
    }

    public ProveedorAfiliado(int idProveedor, String nit, String nombreProveedor, String direccion, String telefono,
    String email, String imagenURL, String catNombre, String nombrePais, int idCategoriaEmpresa, int estado) {
        this.idProveedor = idProveedor;
        this.nit = nit;
        this.nombreProveedor = nombreProveedor;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.imagenURL = imagenURL;
        this.catNombre = catNombre;
        this.nombrePais = nombrePais;
        this.idCategoriaEmpresa = idCategoriaEmpresa;
        this.estado = estado;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    // Getters y Setters
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

    public String getCatNombre() {
        return catNombre;
    }

    public void setCatNombre(String catNombre) {
        this.catNombre = catNombre;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public int getIdCategoriaEmpresa() {
        return idCategoriaEmpresa;
    }

    public void setIdCategoriaEmpresa(int idCategoriaEmpresa) {
        this.idCategoriaEmpresa = idCategoriaEmpresa;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vw_comercio_afiliado")
public class ComercioAfiliado {

    @Id
    @Column(name = "nit")
    private String nit;

    @Column(name = "nombreEmpresa")
    private String nombreEmpresa;

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

    @Column(name = "nombreRubro")
    private String nombreRubro;

    @Column(name = "nombrePais")
    private String nombrePais;

    // Constructores
    public ComercioAfiliado() {
    }

    public ComercioAfiliado(String nit, String nombreEmpresa, String direccion, String telefono,
                            String email, String imagenURL, String catNombre, String nombrePais,
                            String nombreRubro) {
        this.nit = nit;
        this.nombreEmpresa = nombreEmpresa;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.imagenURL = imagenURL;
        this.catNombre = catNombre;
        this.nombrePais = nombrePais;
        this.nombreRubro = nombreRubro;
    }

    // Getters y Setters
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

    public String getNombreRubro() {
        return nombreRubro;
    }

    public void setNombreRubro(String nombreRubro) {
        this.nombreRubro = nombreRubro;
    }

    @Override
    public String toString() {
        return "ComercioAfiliado{" +
                "nit='" + nit + '\'' +
                ", nombreEmpresa='" + nombreEmpresa + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", imagenURL='" + imagenURL + '\'' +
                ", catNombre='" + catNombre + '\'' +
                ", nombrePais='" + nombrePais + '\'' +
                '}';
    }
}

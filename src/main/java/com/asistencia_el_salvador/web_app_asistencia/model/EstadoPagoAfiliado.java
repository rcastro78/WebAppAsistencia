package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_estado_pago_afiliados")
public class EstadoPagoAfiliado {

    @Id
    @Column(name = "DUI", length = 10)
    private String dui;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "ejecutivoAsignado", length = 10)
    private String ejecutivoAsignado;

    @Column(name = "createdBy", length = 45)
    private String createdBy;

    @Column(name = "fechaAfiliacion")
    private LocalDateTime fechaAfiliacion;

    @Column(name = "anioUltimoPago", length = 4)
    private String anioUltimoPago;

    @Column(name = "mesUltimoPago")
    private Integer mesUltimoPago;

    @Column(name = "estadoPago", length = 20)
    private String estadoPago;

    public EstadoPagoAfiliado() {
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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

    public String getEjecutivoAsignado() {
        return ejecutivoAsignado;
    }

    public void setEjecutivoAsignado(String ejecutivoAsignado) {
        this.ejecutivoAsignado = ejecutivoAsignado;
    }

    public LocalDateTime getFechaAfiliacion() {
        return fechaAfiliacion;
    }

    public void setFechaAfiliacion(LocalDateTime fechaAfiliacion) {
        this.fechaAfiliacion = fechaAfiliacion;
    }

    public String getAnioUltimoPago() {
        return anioUltimoPago;
    }

    public void setAnioUltimoPago(String anioUltimoPago) {
        this.anioUltimoPago = anioUltimoPago;
    }

    public Integer getMesUltimoPago() {
        return mesUltimoPago;
    }

    public void setMesUltimoPago(Integer mesUltimoPago) {
        this.mesUltimoPago = mesUltimoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "EstadoPagoAfiliado{" +
                "dui='" + dui + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", ejecutivoAsignado='" + ejecutivoAsignado + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }
}
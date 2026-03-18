package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name="vw_afiliado_solicitud_asistencia_proveedor")
public class AfiliadoSolicitudAsistenciaProv {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "duiAfiliado", length = 45)
    private String duiAfiliado;
    @Column(name = "nombreCompleto")
    private String nombreAfiliado;
    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "idAsistencia")
    private Integer idAsistencia;
    @Column(name = "nombreCobertura", length = 45)
    private String nombreCobertura;
    @Column(name = "idProveedor")
    private Integer idProveedor;
    @Column(name = "nombreProveedor", length = 45)
    private String nombreProveedor;
    @Column(name = "imagenURL", length = 45)
    private String imagenURL;
    @Column(name = "detalle")
    private String detalle;
    @Column(name = "fechaAsistencia")
    private LocalDate fechaAsistencia;

    @Column(name = "horaAsistencia")
    private LocalTime horaAsistencia;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "estado", length = 45)
    private String estado = "0";
    @Column(name = "tarifaAplicada")
    private Double tarifaAplicada;
    @Column(name = "costosExtra")
    private Double costosExtra;
    @Column(name = "calificacion")
    private Double calificacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getNombreCompleto() {
        return nombreAfiliado;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreAfiliado = nombreCompleto;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Integer idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public void setNombreCobertura(String nombreCobertura) {
        this.nombreCobertura = nombreCobertura;
    }

    public String getNombreAfiliado() {
        return nombreAfiliado;
    }

    public void setNombreAfiliado(String nombreAfiliado) {
        this.nombreAfiliado = nombreAfiliado;
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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }



    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTarifaAplicada() {
        return tarifaAplicada;
    }

    public void setTarifaAplicada(Double tarifaAplicada) {
        this.tarifaAplicada = tarifaAplicada;
    }

    public Double getCostosExtra() {
        return costosExtra;
    }

    public void setCostosExtra(Double costosExtra) {
        this.costosExtra = costosExtra;
    }

    public LocalDate getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(LocalDate fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public LocalTime getHoraAsistencia() {
        return horaAsistencia;
    }

    public void setHoraAsistencia(LocalTime horaAsistencia) {
        this.horaAsistencia = horaAsistencia;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
}


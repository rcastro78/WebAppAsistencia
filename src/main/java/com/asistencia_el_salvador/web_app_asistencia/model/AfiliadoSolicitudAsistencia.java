package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "afiliado_solicitud_asistencia")
public class AfiliadoSolicitudAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "duiAfiliado", length = 10)
    private String duiAfiliado;

    @Column(name = "idPlan")
    private Integer idPlan;

    @Column(name = "idAsistencia")
    private Integer idAsistencia;

    @Column(name = "idProveedor", length = 45)
    private String idProveedor;

    @Column(name = "detalle", columnDefinition = "TINYTEXT")
    private String detalle;

    @Column(name = "fechaAsistencia")
    private LocalDate fechaAsistencia;

    @Column(name = "horaAsistencia")
    private LocalTime horaAsistencia;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "registradoPor", length = 45)
    private String registradoPor;

    @Column(name = "estado", length = 45)
    private String estado = "0";

    @Column(name = "observacion", columnDefinition = "MEDIUMTEXT")
    private String observacion;

    @Column(name = "costosExtra")
    private Double costosExtra;

    @Column(name = "tarifaAplicada")
    private Double tarifaAplicada;

    @Column(name = "calificacion")
    private Double calificacion;

    // Constructor sin parámetros
    public AfiliadoSolicitudAsistencia() {
    }

    // Constructor con parámetros
    public AfiliadoSolicitudAsistencia(String duiAfiliado, Integer idPlan, Integer idAsistencia,
                                       String idProveedor, String detalle, LocalDate fechaAsistencia,
                                       LocalTime horaAsistencia, String registradoPor) {
        this.duiAfiliado = duiAfiliado;
        this.idPlan = idPlan;
        this.idAsistencia = idAsistencia;
        this.idProveedor = idProveedor;
        this.detalle = detalle;
        this.fechaAsistencia = fechaAsistencia;
        this.horaAsistencia = horaAsistencia;
        this.registradoPor = registradoPor;
    }

    // Getters y Setters
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

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
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

    public String getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(String registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getCostosExtra() {
        return costosExtra;
    }

    public void setCostosExtra(Double costosExtra) {
        this.costosExtra = costosExtra;
    }

    public Double getTarifaAplicada() {
        return tarifaAplicada;
    }

    public void setTarifaAplicada(Double tarifaAplicada) {
        this.tarifaAplicada = tarifaAplicada;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    // Método lifecycle para establecer createdAt automáticamente
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "0";
        }
    }


}

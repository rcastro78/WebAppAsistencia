package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "promociones",
        schema = "asistenciaDB",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "qrCode")
        }
)
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NITEmpresa", length = 45)
    private String nitEmpresa;

    @Column(name = "idPlan")
    private Integer idPlan;

    @Column(name = "nombreDescuento", length = 45)
    private String nombreDescuento;

    @Column(name = "tipoDescuento")
    private Integer tipoDescuento;

    @Column(name = "valorDescuento", precision = 9, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "activo")
    private Integer activo;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    @Column(name = "qrCode", length = 100, unique = true)
    private String qrCode;

    @Column(name = "canjesPorUsuario")
    private Integer canjesPorUsuario;

    @Column(name = "maxCanjes")
    private Integer maxCanjes;

    public Promocion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNitEmpresa() {
        return nitEmpresa;
    }

    public void setNitEmpresa(String nitEmpresa) {
        this.nitEmpresa = nitEmpresa;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombreDescuento() {
        return nombreDescuento;
    }

    public void setNombreDescuento(String nombreDescuento) {
        this.nombreDescuento = nombreDescuento;
    }

    public Integer getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(Integer tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(BigDecimal valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getCanjesPorUsuario() {
        return canjesPorUsuario;
    }

    public void setCanjesPorUsuario(Integer canjesPorUsuario) {
        this.canjesPorUsuario = canjesPorUsuario;
    }

    public Integer getMaxCanjes() {
        return maxCanjes;
    }

    public void setMaxCanjes(Integer maxCanjes) {
        this.maxCanjes = maxCanjes;
    }
}

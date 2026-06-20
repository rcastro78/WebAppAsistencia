package com.asistencia_el_salvador.web_app_asistencia.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromocionDTO {

    private Integer id;
    private String nitEmpresa;
    private Integer idPlan;
    private String nombrePlan;
    private String nombreDescuento;
    private Integer tipoDescuento;
    private BigDecimal valorDescuento;
    private Integer activo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String qrCode;
    private Integer canjesPorUsuario;
    private Integer maxCanjes;

    public PromocionDTO() {
    }

    public PromocionDTO(Integer id, String nitEmpresa, Integer idPlan, String nombrePlan,
                        String nombreDescuento, Integer tipoDescuento, BigDecimal valorDescuento,
                        Integer activo, LocalDateTime fechaInicio, LocalDateTime fechaFin,
                        String qrCode, Integer canjesPorUsuario, Integer maxCanjes) {
        this.id = id;
        this.nitEmpresa = nitEmpresa;
        this.idPlan = idPlan;
        this.nombrePlan = nombrePlan;
        this.nombreDescuento = nombreDescuento;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.activo = activo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.qrCode = qrCode;
        this.canjesPorUsuario = canjesPorUsuario;
        this.maxCanjes = maxCanjes;

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

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
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

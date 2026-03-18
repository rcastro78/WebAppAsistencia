package com.asistencia_el_salvador.web_app_asistencia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO recibido desde la app móvil para crear o actualizar
 * una AfiliadoSolicitudAsistencia.
 */
public class SolicitudAsistenciaRequest {

    // Requerido solo cuando el rol es ADMIN (1)
    private String duiAfiliado;

    private Integer idAsistencia;   // idCobertura
    private String  idProveedor;
    private Integer idPlan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAsistencia;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaAsistencia;

    private String  detalle;

    // Solo Admin puede enviar estos campos en updates
    private String  estado;
    private Double  tarifaAplicada;
    private Double  costosExtra;
    private String  observacion;

    // Getters & Setters ───────────────────────────────────────

    public String getDuiAfiliado()          { return duiAfiliado; }
    public void   setDuiAfiliado(String v)  { this.duiAfiliado = v; }

    public Integer getIdAsistencia()        { return idAsistencia; }
    public void    setIdAsistencia(Integer v){ this.idAsistencia = v; }

    public String getIdProveedor()          { return idProveedor; }
    public void   setIdProveedor(String v)  { this.idProveedor = v; }

    public Integer getIdPlan()              { return idPlan; }
    public void    setIdPlan(Integer v)     { this.idPlan = v; }

    public LocalDate getFechaAsistencia()       { return fechaAsistencia; }
    public void      setFechaAsistencia(LocalDate v){ this.fechaAsistencia = v; }

    public LocalTime getHoraAsistencia()        { return horaAsistencia; }
    public void      setHoraAsistencia(LocalTime v){ this.horaAsistencia = v; }

    public String getDetalle()              { return detalle; }
    public void   setDetalle(String v)      { this.detalle = v; }

    public String getEstado()               { return estado; }
    public void   setEstado(String v)       { this.estado = v; }

    public Double getTarifaAplicada()       { return tarifaAplicada; }
    public void   setTarifaAplicada(Double v){ this.tarifaAplicada = v; }

    public Double getCostosExtra()          { return costosExtra; }
    public void   setCostosExtra(Double v)  { this.costosExtra = v; }

    public String getObservacion()          { return observacion; }
    public void   setObservacion(String v)  { this.observacion = v; }
}
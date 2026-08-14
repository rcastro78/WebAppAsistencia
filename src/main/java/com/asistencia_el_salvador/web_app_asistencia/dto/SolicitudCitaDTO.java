package com.asistencia_el_salvador.web_app_asistencia.dto;

import java.time.LocalDateTime;

public class SolicitudCitaDTO {

    private String duiDoctor;
    private LocalDateTime fechaProgramada;
    private String motivoConsulta;
    private Integer modalidad;       // 1 = Presencial, 2 = Video
    private Integer idEspecialidad;
    private Integer idCobertura;
    private Integer duracionMinutos;

    // getters y setters
    public String getDuiDoctor() { return duiDoctor; }
    public void setDuiDoctor(String duiDoctor) { this.duiDoctor = duiDoctor; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public Integer getModalidad() { return modalidad; }
    public void setModalidad(Integer modalidad) { this.modalidad = modalidad; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public Integer getIdCobertura() { return idCobertura; }
    public void setIdCobertura(Integer idCobertura) { this.idCobertura = idCobertura; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}
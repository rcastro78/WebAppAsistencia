package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "vw_med_citas")
public class MedCita {

    @Id
    @Column(name = "idConsulta")
    private Integer idConsulta;

    @Column(name = "roomId")
    private String roomId;

    @Column(name = "fechaProgramada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;



    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "duracionMinutos")
    private Integer duracionMinutos;

    @Column(name = "idEstadoConsulta")
    private Integer idEstadoConsulta;

    @Column(name = "estadoConsulta")
    private String estadoConsulta;

    @Column(name = "idTipo")
    private Integer idTipo;

    @Column(name = "tipoConsulta")
    private String tipoConsulta;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "duiDoctor")
    private String duiDoctor;

    @Column(name = "doctor")
    private String doctor;

    @Column(name = "emailDoctor")
    private String emailDoctor;

    @Column(name = "telefonoDoctor")
    private String telefonoDoctor;

    @Column(name = "idEspecialidad")
    private Integer idEspecialidad;

    @Column(name = "nombreEspecialidad")
    private String nombreEspecialidad;

    @Column(name = "duiAfiliado")
    private String duiAfiliado;

    @Column(name = "afiliado")
    private String afiliado;

    @Column(name = "telefonoAfiliado")
    private String telefonoAfiliado;

    @Column(name = "emailAfiliado")
    private String emailAfiliado;

    @Column(name = "calificacion")
    private BigDecimal calificacion;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "rechazada")
    private Integer rechazada;

    @Column(name = "motivoRechazo")
    private String motivoRechazo;

    @Column(name = "estadoAgenda")
    private String estadoAgenda;

    @Column(name = "idCobertura")
    private Integer idCobertura;

    public MedCita() {
    }

    public Integer getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Integer idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
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

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Integer getIdEstadoConsulta() {
        return idEstadoConsulta;
    }

    public void setIdEstadoConsulta(Integer idEstadoConsulta) {
        this.idEstadoConsulta = idEstadoConsulta;
    }

    public String getEstadoConsulta() {
        return estadoConsulta;
    }

    public void setEstadoConsulta(String estadoConsulta) {
        this.estadoConsulta = estadoConsulta;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDuiDoctor() {
        return duiDoctor;
    }

    public void setDuiDoctor(String duiDoctor) {
        this.duiDoctor = duiDoctor;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getEmailDoctor() {
        return emailDoctor;
    }

    public void setEmailDoctor(String emailDoctor) {
        this.emailDoctor = emailDoctor;
    }

    public String getTelefonoDoctor() {
        return telefonoDoctor;
    }

    public void setTelefonoDoctor(String telefonoDoctor) {
        this.telefonoDoctor = telefonoDoctor;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(String afiliado) {
        this.afiliado = afiliado;
    }

    public String getTelefonoAfiliado() {
        return telefonoAfiliado;
    }

    public void setTelefonoAfiliado(String telefonoAfiliado) {
        this.telefonoAfiliado = telefonoAfiliado;
    }

    public String getEmailAfiliado() {
        return emailAfiliado;
    }

    public void setEmailAfiliado(String emailAfiliado) {
        this.emailAfiliado = emailAfiliado;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getRechazada() {
        return rechazada;
    }

    public void setRechazada(Integer rechazada) {
        this.rechazada = rechazada;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getEstadoAgenda() {
        return estadoAgenda;
    }

    public void setEstadoAgenda(String estadoAgenda) {
        this.estadoAgenda = estadoAgenda;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(Integer idCobertura) {
        this.idCobertura = idCobertura;
    }
}
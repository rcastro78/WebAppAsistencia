package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_notificaciones_afiliado_vendedor")
public class NotificacionesAfiliadoVendedor {
    @Id
    @Column(name = "idNotificacion")
    private Integer idNotificacion;
    @Column(name = "notificacion")
    private String notificacion;
    @Column(name = "dui")
    private String dui;
    @Column(name = "fecha")
    private LocalDateTime fecha;
    @Column(name = "ejecutivoAsignado")
    private String ejecutivoAsignado;

    public Integer getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEjecutivoAsignado() {
        return ejecutivoAsignado;
    }

    public void setEjecutivoAsignado(String ejecutivoAsignado) {
        this.ejecutivoAsignado = ejecutivoAsignado;
    }
}

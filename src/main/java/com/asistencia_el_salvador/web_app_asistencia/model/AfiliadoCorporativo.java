package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "afiliado_corporativo")
public class AfiliadoCorporativo {
    @Id
    @Column(name = "duiAfiliado", length = 10, nullable = false, unique = true)
    private String duiAfiliado;
    @Column(name = "NITCliente", length = 100)
    private String NITCliente;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fechaAfiliacion")
    private LocalDate fechaAfiliacion;
    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt", length = 45)
    private LocalDateTime deletedAt;

    @Column(name = "estado")
    private Integer estado;

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getNITCliente() {
        return NITCliente;
    }

    public void setNITCliente(String NITCliente) {
        this.NITCliente = NITCliente;
    }

    public LocalDate getFechaAfiliacion() {
        return fechaAfiliacion;
    }

    public void setFechaAfiliacion(LocalDate fechaAfiliacion) {
        this.fechaAfiliacion = fechaAfiliacion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}

package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "vw_equipo_ventas")
public class VwEquipoVentas {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "duiSupervisor")
    private String duiSupervisor;

    @Column(name = "nombreSupervisor")
    private String nombreSupervisor;

    @Column(name = "duiVendedor")
    private String duiVendedor;

    @Column(name = "nombreVendedor")
    private String nombreVendedor;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "zonaCobertura")
    private String zonaCobertura;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    public VwEquipoVentas() {}

    public VwEquipoVentas(Integer id, String duiSupervisor, String nombreSupervisor,
                             String duiVendedor, String nombreVendedor,
                             Integer estado, String zonaCobertura, LocalDateTime createdAt) {
        this.id = id;
        this.duiSupervisor = duiSupervisor;
        this.nombreSupervisor = nombreSupervisor;
        this.duiVendedor = duiVendedor;
        this.nombreVendedor = nombreVendedor;
        this.estado = estado;
        this.zonaCobertura = zonaCobertura;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDuiSupervisor() {
        return duiSupervisor;
    }

    public void setDuiSupervisor(String duiSupervisor) {
        this.duiSupervisor = duiSupervisor;
    }

    public String getNombreSupervisor() {
        return nombreSupervisor;
    }

    public void setNombreSupervisor(String nombreSupervisor) {
        this.nombreSupervisor = nombreSupervisor;
    }

    public String getDuiVendedor() {
        return duiVendedor;
    }

    public void setDuiVendedor(String duiVendedor) {
        this.duiVendedor = duiVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = zonaCobertura;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "VistaEquipoVentas{" +
                "id=" + id +
                ", duiSupervisor='" + duiSupervisor + '\'' +
                ", nombreSupervisor='" + nombreSupervisor + '\'' +
                ", duiVendedor='" + duiVendedor + '\'' +
                ", nombreVendedor='" + nombreVendedor + '\'' +
                ", estado=" + estado +
                ", zonaCobertura='" + zonaCobertura + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
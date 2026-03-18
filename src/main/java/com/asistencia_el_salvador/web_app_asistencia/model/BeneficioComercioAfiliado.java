package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "beneficios_comercio_afiliado")
public class BeneficioComercioAfiliado {

    @Id
    @Column(name = "idBeneficio", nullable = false)
    private Long idBeneficio;

    @Column(name = "NITComercio", length = 45)
    private String nitComercio;

    @Column(name = "nombreBeneficio")
    private String nombreBeneficio;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "fechaInicio")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @Column(name = "fechaFin")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    // Getters y Setters
    public Long getIdBeneficio() { return idBeneficio; }
    public void setIdBeneficio(Long idBeneficio) { this.idBeneficio = idBeneficio; }

    public String getNitComercio() { return nitComercio; }
    public void setNitComercio(String nitComercio) { this.nitComercio = nitComercio; }

    public String getNombreBeneficio() { return nombreBeneficio; }
    public void setNombreBeneficio(String nombreBeneficio) { this.nombreBeneficio = nombreBeneficio; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "med_horarios_citas")
public class MedHorariosCitas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "DUIDoctor", nullable = false, length = 45)
    private String duiDoctor;

    @Column(name = "dia", nullable = false, length = 1)
    private String dia;

    @Column(name = "horaInicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horaFin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "createdAt", updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public MedHorariosCitas() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDuiDoctor() { return duiDoctor; }
    public void setDuiDoctor(String duiDoctor) { this.duiDoctor = duiDoctor; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
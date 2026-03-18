package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Embeddable
public class MedHorariosCitasId implements Serializable {

    @Column(name = "DUIDoctor", nullable = false, length = 45)
    private String duiDoctor;

    @Column(name = "dia", nullable = false, length = 1)
    private String dia;

    @Column(name = "horaInicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horaFin", nullable = false)
    private LocalTime horaFin;

    public MedHorariosCitasId() {}

    public MedHorariosCitasId(String duiDoctor, String dia, LocalTime horaInicio, LocalTime horaFin) {
        this.duiDoctor = duiDoctor;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getDuiDoctor() { return duiDoctor; }
    public void setDuiDoctor(String duiDoctor) { this.duiDoctor = duiDoctor; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedHorariosCitasId)) return false;
        MedHorariosCitasId that = (MedHorariosCitasId) o;
        return Objects.equals(duiDoctor, that.duiDoctor) &&
                Objects.equals(dia, that.dia) &&
                Objects.equals(horaInicio, that.horaInicio) &&
                Objects.equals(horaFin, that.horaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duiDoctor, dia, horaInicio, horaFin);
    }
}
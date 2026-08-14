package com.asistencia_el_salvador.web_app_asistencia.model;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Column;


@Entity
@Table(name = "doctor_clinica")
public class DoctorClinica {

    @EmbeddedId
    private DoctorClinicaId id;

    @Column(name = "estado")
    private Integer estado;


    public DoctorClinica() {
    }

    public DoctorClinica(DoctorClinicaId id, Integer estado) {
        this.id = id;
        this.estado = estado;
    }

    public DoctorClinicaId getId() {
        return id;
    }

    public void setId(DoctorClinicaId id) {
        this.id = id;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

}


package com.asistencia_el_salvador.web_app_asistencia.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class DoctorClinicaId implements Serializable {

    @Column(name = "idClinica", nullable = false)
    private Integer idClinica;

    @Column(name = "DUIDoctor", nullable = false, length = 45)
    private String duiDoctor;

    public DoctorClinicaId() {
    }

    public DoctorClinicaId(Integer idClinica, String duiDoctor) {
        this.idClinica = idClinica;
        this.duiDoctor = duiDoctor;
    }

    public Integer getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Integer idClinica) {
        this.idClinica = idClinica;
    }

    public String getDuiDoctor() {
        return duiDoctor;
    }

    public void setDuiDoctor(String duiDoctor) {
        this.duiDoctor = duiDoctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoctorClinicaId that = (DoctorClinicaId) o;
        return Objects.equals(idClinica, that.idClinica) &&
                Objects.equals(duiDoctor, that.duiDoctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClinica, duiDoctor);
    }
}
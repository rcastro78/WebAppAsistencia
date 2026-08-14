package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.DoctorClinica;
import com.asistencia_el_salvador.web_app_asistencia.model.DoctorClinicaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorClinicaRepository extends JpaRepository<DoctorClinica, Long> {
    boolean existsById(DoctorClinicaId doctorClinicaId);
}

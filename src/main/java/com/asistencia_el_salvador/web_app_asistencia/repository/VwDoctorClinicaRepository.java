package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.VwClinicaDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VwDoctorClinicaRepository extends JpaRepository<VwClinicaDoctor, Long> {
    //Obtener clinicas del doctor
    List<VwClinicaDoctor> findByDui(String dui);
}

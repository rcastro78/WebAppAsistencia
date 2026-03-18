package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.MedHorariosCitas;
import com.asistencia_el_salvador.web_app_asistencia.model.MedHorariosCitasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MedHorariosCitasRepository extends JpaRepository<MedHorariosCitas, Integer> {
    List<MedHorariosCitas> findByDuiDoctor(String duiDoctor);
    List<MedHorariosCitas> findByDia(String dia);
    void deleteByDuiDoctor(String duiDoctor);
}
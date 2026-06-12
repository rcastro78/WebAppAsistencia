package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Coparticipacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoparticipacionRepository extends JpaRepository<Coparticipacion, Long> {
    Coparticipacion findById(Integer id);
    Coparticipacion findByIdPlan(Integer id);
}

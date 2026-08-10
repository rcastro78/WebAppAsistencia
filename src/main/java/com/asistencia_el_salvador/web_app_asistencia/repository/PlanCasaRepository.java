package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanCasa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCasaRepository extends JpaRepository<PlanCasa, Long> {
    PlanCasa findById(long id);
    List<PlanCasa> findByEstado(int estado);
    PlanCasa findByNIC(String nic);
    //PlanCasa findByNombrePlan(String nombrePlan);
}

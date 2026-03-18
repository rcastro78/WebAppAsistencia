package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    List<Plan> findByEstado(int estado);
    List<Plan> findByNombrePlan(String nombre);
    Page<Plan> findByEstadoTrue(Pageable pageable);
    long countByEstado(int estado);
    List<Plan> findByIdPais(int idPais);
}
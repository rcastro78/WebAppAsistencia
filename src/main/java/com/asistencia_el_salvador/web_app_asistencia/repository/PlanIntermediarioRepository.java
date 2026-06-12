package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanIntermediario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanIntermediarioRepository
        extends JpaRepository<PlanIntermediario, Integer> {
PlanIntermediario findByNitIntermediario(String nit);
List<PlanIntermediario> findAll();
List<PlanIntermediario> findAllByEstado(int estado);
}

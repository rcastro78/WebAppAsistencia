package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanCoparticipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCoparticipeRepository extends JpaRepository<PlanCoparticipe, Long> {
    PlanCoparticipe findByNitCoparticipe(String nit);
    List<PlanCoparticipe> findAll();
    List<PlanCoparticipe> findAllByEstado(int estado);
}

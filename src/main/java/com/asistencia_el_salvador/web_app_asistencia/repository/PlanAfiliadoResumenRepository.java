package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliadoResumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanAfiliadoResumenRepository extends JpaRepository<PlanAfiliadoResumen,String> {
    Optional<PlanAfiliadoResumen> findById(String dui);
    List <PlanAfiliadoResumen> findAllByDui(String dui);
}

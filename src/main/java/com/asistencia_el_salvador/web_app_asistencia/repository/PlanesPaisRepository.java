package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanesPais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanesPaisRepository  extends JpaRepository<PlanesPais,String> {
    List<PlanesPais> findByIdPais(int idPais);
    PlanesPais findByIdPlan(int idPlan);
}


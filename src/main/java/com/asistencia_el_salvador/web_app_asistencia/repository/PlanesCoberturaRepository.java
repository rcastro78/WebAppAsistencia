package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanesCobertura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanesCoberturaRepository extends JpaRepository<PlanesCobertura,String> {
    List<PlanesCobertura> findByIdPlan(Integer idPlan);
    List<PlanesCobertura> findByIdPais(Integer idPais);
    PlanesCobertura findByIdCobertura(Integer idCobertura);
    PlanesCobertura findByIdCoberturaAndIdPlan(Integer idCobertura, Integer idPlan);
}

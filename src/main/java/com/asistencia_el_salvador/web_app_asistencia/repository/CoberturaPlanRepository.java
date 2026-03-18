package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.CoberturaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaPlanRepository extends JpaRepository<CoberturaPlan,String> {
    public List<CoberturaPlan> findByEstado(int estado);
    public CoberturaPlan findByIdCoberturaAndIdPlan(int idCobertura, int idPlan);
}

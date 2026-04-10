package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public long contarPlanesActivos(){
        return planRepository.countByEstado(1);
    }

    public List<Plan> listarActivos(){
        return planRepository.findByEstado(1);
    }

    public List<Plan> listarPorPais(int idPais){
        return planRepository.findByIdPais(idPais);
    }

    public Page<Plan> listarPaginados(Pageable pageable){
        return planRepository.findByEstadoTrue(pageable);
    }

    public Optional<Plan> getPlanById(Integer id){
        return planRepository.findById(id);
    }

    public List<Plan> getPlanIdByNombrePlan(String nombre){
        return planRepository.findByNombrePlan(nombre);
    }

    //Crear nuevo plan
    public Plan savePlan(Plan p){
        return planRepository.save(p);
    }
}

package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanCoparticipe;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanCoparticipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanCoparticipeService {
    private final PlanCoparticipeRepository planCoparticipeRepository;
    public PlanCoparticipeService(PlanCoparticipeRepository planCoparticipeRepository) {
        this.planCoparticipeRepository = planCoparticipeRepository;
    }

    public PlanCoparticipe buscarPorNit(String nit) {
        return  planCoparticipeRepository.findByNitCoparticipe(nit);
    }
    public List<PlanCoparticipe> mostrarTodos() {
        return planCoparticipeRepository.findAll();
    }
    public List<PlanCoparticipe> buscarActivos() {
        return planCoparticipeRepository.findAllByEstado(1);
    }

}

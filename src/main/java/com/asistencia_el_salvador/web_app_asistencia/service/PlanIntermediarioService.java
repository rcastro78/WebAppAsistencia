package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanIntermediario;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanIntermediarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanIntermediarioService {
    private final PlanIntermediarioRepository planIntermediarioRepository;
    public PlanIntermediarioService(PlanIntermediarioRepository planIntermediarioRepository) {
        this.planIntermediarioRepository = planIntermediarioRepository;
    }
    public List<PlanIntermediario> buscarActivos() {
        return planIntermediarioRepository.findAllByEstado(1);
    }
    public List<PlanIntermediario> mostrarTodos() {
        return planIntermediarioRepository.findAll();
    }
    public PlanIntermediario buscarPlanIntermediarioPorNit(String nit) {
        return planIntermediarioRepository.findByNitIntermediario(nit);
    }

}

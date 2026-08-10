package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanCasa;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanCasaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanCasaService {
    private final PlanCasaRepository planCasaRepository;
    private final PlanRepository planRepository;
    public PlanCasaService(PlanCasaRepository planCasaRepository,
                           PlanRepository planRepository) {
        this.planCasaRepository = planCasaRepository;
        this.planRepository = planRepository;
    }
    public List<PlanCasa> findByEstado(int estado) {
        return planCasaRepository.findByEstado(estado);
    }
    public Plan findByNombrePlan(String nombrePlan) {
        return planRepository.findByNombrePlan(nombrePlan).get(0);
    }
    public PlanCasa save(PlanCasa planCasa) {
        return planCasaRepository.save(planCasa);
    }

    public PlanCasa findByNIC(String nic) {
        return planCasaRepository.findByNIC(nic);
    }

    public void eliminar(long id) {
        if (!planCasaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un plan con id: " + id);
        }
        planCasaRepository.deleteById(id);
    }
}

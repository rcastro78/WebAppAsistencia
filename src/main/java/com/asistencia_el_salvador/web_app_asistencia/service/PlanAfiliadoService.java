package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.dto.PlanAfiliadoDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanAfiliadoService {
    private final PlanAfiliadoRepository planAfiliadoRepository;
    private final PlanRepository planRepository;

    public PlanAfiliadoService(PlanAfiliadoRepository planAfiliadoRepository,
                               PlanRepository planRepository) {
        this.planAfiliadoRepository = planAfiliadoRepository;
        this.planRepository = planRepository;
    }

    public List<PlanAfiliado> findByDui(String id) {
        return planAfiliadoRepository.findByDui(id);
    }

    public List<PlanAfiliadoDTO> findByDui(String dui, boolean nuevo) {
        return planAfiliadoRepository.findByDui(dui)
                .stream()
                .map(p -> {
                    int idPlan = p.getIdPlan();
                    Plan plan = planRepository.findById(idPlan).get();
                    return new PlanAfiliadoDTO(p, plan.getNombrePlan());
                })
                .collect(Collectors.toList());
    }
}

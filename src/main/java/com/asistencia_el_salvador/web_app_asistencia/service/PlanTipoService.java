package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanTipo;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanTipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanTipoService {
    private final PlanTipoRepository planTipoRepository;
    public PlanTipoService(PlanTipoRepository planTipoRepository) {
        this.planTipoRepository = planTipoRepository;
    }
    public List<PlanTipo> listarActivos() {
        return planTipoRepository.findAllByEstado(1);
    }
}

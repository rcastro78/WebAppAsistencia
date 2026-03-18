package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.CoberturaPlan;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaPlanRepository;
import org.springframework.stereotype.Service;

@Service
public class CoberturaPlanService {
    private CoberturaPlanRepository coberturaPlanRepository;

    public CoberturaPlanService(CoberturaPlanRepository coberturaPlanRepository) {
        this.coberturaPlanRepository = coberturaPlanRepository;
    }

    public CoberturaPlan buscarCobertura(int idCobertura, int idPlan){
        return coberturaPlanRepository.findByIdCoberturaAndIdPlan(idCobertura,idPlan);
    }

}

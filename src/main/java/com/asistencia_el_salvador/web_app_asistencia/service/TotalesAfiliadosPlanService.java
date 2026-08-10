package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.TotalesAfiliadosPlan;
import com.asistencia_el_salvador.web_app_asistencia.repository.TotalesAfiliadosPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TotalesAfiliadosPlanService {
    private final TotalesAfiliadosPlanRepository repository;
    public TotalesAfiliadosPlanService(TotalesAfiliadosPlanRepository repository) {
        this.repository = repository;
    }
    public List<TotalesAfiliadosPlan> getTotalidadPlanesEjecutivo(String dui) {
        return repository.findByEjecutivo(dui);
    }
}

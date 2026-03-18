package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanesPais;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanesPaisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanesPaisService {
    private final PlanesPaisRepository repository;

    public PlanesPaisService(PlanesPaisRepository repository) {
        this.repository = repository;
    }

    public List<PlanesPais> getPlanesPais(int idPais){
        return repository.findByIdPais(idPais);
    }

    public PlanesPais getPlanesByIdPlan(int idPlan){
        return repository.findByIdPlan(idPlan);
    }
}

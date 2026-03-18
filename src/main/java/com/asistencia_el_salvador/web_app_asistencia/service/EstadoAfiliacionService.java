package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoAfiliacion;
import com.asistencia_el_salvador.web_app_asistencia.repository.EstadoAfiliacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoAfiliacionService {
    private final EstadoAfiliacionRepository estadoAfiliacionRepository;

    public EstadoAfiliacionService(EstadoAfiliacionRepository estadoAfiliacionRepository) {
        this.estadoAfiliacionRepository = estadoAfiliacionRepository;
    }
    public List<EstadoAfiliacion> listarTodos(){
        return estadoAfiliacionRepository.findAll();
    }

}

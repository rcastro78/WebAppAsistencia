package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoSolicitudServicio;
import com.asistencia_el_salvador.web_app_asistencia.repository.EstadoSolicitudServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoSolicitudServicioService {
    private final EstadoSolicitudServicioRepository repository;

    public EstadoSolicitudServicioService(EstadoSolicitudServicioRepository repository) {
        this.repository = repository;
    }

    public List<EstadoSolicitudServicio> listarTodos(){
        return repository.findAll();
    }
}

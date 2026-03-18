package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoContrato;
import com.asistencia_el_salvador.web_app_asistencia.repository.EstadoContratoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoContratoService {
    private final EstadoContratoRepository repository;

    public EstadoContratoService(EstadoContratoRepository repository) {
        this.repository = repository;
    }

    public List<EstadoContrato> listarTodos(){
        return repository.findAll();
    }
}

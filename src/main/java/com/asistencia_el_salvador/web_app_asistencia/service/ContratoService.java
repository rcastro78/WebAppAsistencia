package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Contrato;
import com.asistencia_el_salvador.web_app_asistencia.repository.ContratoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratoService {
    private final ContratoRepository repository;

    public ContratoService(ContratoRepository repository) {
        this.repository = repository;
    }

    public Contrato buscarPorDUI(String dui){
        return repository.findByDui(dui);
    }
}

package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoberturaService {
    private final CoberturaRepository repository;

    public CoberturaService(CoberturaRepository repository) {
        this.repository = repository;
    }

    public List<Cobertura> listarActivas(){
        return repository.findByEstado(1);
    }
}

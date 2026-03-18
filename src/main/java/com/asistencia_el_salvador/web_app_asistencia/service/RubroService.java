package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Rubro;
import com.asistencia_el_salvador.web_app_asistencia.repository.RubroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RubroService {
    private final RubroRepository repository;

    public RubroService(RubroRepository repository) {
        this.repository = repository;
    }
    public List<Rubro> listarTodos(){
        return repository.findAll();
    }

}

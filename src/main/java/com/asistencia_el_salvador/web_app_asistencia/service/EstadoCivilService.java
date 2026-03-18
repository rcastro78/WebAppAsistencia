package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoCivil;
import com.asistencia_el_salvador.web_app_asistencia.repository.EstadoCivilRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoCivilService {
    private final EstadoCivilRepository repository;

    public EstadoCivilService(EstadoCivilRepository repository) {
        this.repository = repository;
    }
    public List<EstadoCivil> getEstados(){
        return repository.findAll();
    }

    public EstadoCivil getByIdEstadoCivil(int id){
        return repository.getReferenceById(id);
    }

    public EstadoCivil guardar(EstadoCivil e){
        return repository.save(e);
    }
}

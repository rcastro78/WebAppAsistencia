package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoHogar;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoHogarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoHogarService {
    private final AfiliadoHogarRepository repository;

    public AfiliadoHogarService(AfiliadoHogarRepository repository) {
        this.repository = repository;
    }
    public AfiliadoHogar buscarPorDui(String dui){
        return repository.findByDuiAfiliado(dui);
    }


    public AfiliadoHogar guardar(AfiliadoHogar a){
        return repository.save(a);
    }

    public void eliminar(AfiliadoHogar a){
        repository.delete(a);
    }
}

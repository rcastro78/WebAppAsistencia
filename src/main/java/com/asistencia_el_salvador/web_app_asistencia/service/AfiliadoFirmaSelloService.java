package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoFirmaSello;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoFirmaSelloRepository;
import org.springframework.stereotype.Service;

@Service
public class AfiliadoFirmaSelloService {
    private final AfiliadoFirmaSelloRepository repository;

    public AfiliadoFirmaSelloService(AfiliadoFirmaSelloRepository repository) {
        this.repository = repository;
    }

    public AfiliadoFirmaSello buscarPorDui(String dui){
        return repository.findByDui(dui);
    }

    public AfiliadoFirmaSello guardar(AfiliadoFirmaSello a){
        return repository.save(a);
    }
}

package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoCorporativoRepository;

public class AfiliadoCorporativoService {
    private AfiliadoCorporativoRepository repository;
    public AfiliadoCorporativoService(AfiliadoCorporativoRepository repository) {
        this.repository = repository;
    }
    public AfiliadoCorporativo guardar(AfiliadoCorporativo afiliadoCorporativo) {
        return repository.save(afiliadoCorporativo);
    }


}

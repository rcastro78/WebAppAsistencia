package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoCorporativoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoCorporativoService {
    private AfiliadoCorporativoRepository repository;
    public AfiliadoCorporativoService(AfiliadoCorporativoRepository repository) {
        this.repository = repository;
    }
    public AfiliadoCorporativo guardar(AfiliadoCorporativo afiliadoCorporativo) {
        return repository.save(afiliadoCorporativo);
    }
    public List<AfiliadoCorporativo> buscarTodosActivos(String nit) {
        return repository.findAllByNITClienteAndEstado(nit,1);
    }
    public long afiliadosConsultaron(String nit) {
        return repository.countAfiliadosConConsultaConcretada(nit);
    }
    public long afiliadosCitaProgramada(String nit) {
        return repository.countAfiliadosConConsultaProgramada(nit);
    }



}

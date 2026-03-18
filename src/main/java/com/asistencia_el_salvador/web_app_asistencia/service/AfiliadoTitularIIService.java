package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoTitular2;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoTitularIIRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoTitularIIService {
    private final AfiliadoTitularIIRepository repository;

    public AfiliadoTitularIIService(AfiliadoTitularIIRepository repository) {
        this.repository = repository;
    }


    public AfiliadoTitular2 buscarPorDuiAfiliado(String duiAfiliado) {
        return repository.findByDuiAfiliado(duiAfiliado);
    }

    public AfiliadoTitular2 guardar(AfiliadoTitular2 a){
        return repository.save(a);
    }
}

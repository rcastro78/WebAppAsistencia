package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPatrocinado;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoPatrocinadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AfiliadoPatrocinadoService {
    private AfiliadoPatrocinadoRepository repository;

    public AfiliadoPatrocinadoService(AfiliadoPatrocinadoRepository repository) {
        this.repository = repository;
    }

    public Page<AfiliadoPatrocinado> listarPatrocinados(String dui, Pageable pageable) {
        return repository.findAllByPatrocinadorDUI(dui,pageable);
    }
}

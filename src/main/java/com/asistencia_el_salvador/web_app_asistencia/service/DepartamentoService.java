package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Departamento;
import com.asistencia_el_salvador.web_app_asistencia.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> getDepartamentosByPais(Integer idPais) {
        return departamentoRepository.findByIdPais(idPais);
    }
}
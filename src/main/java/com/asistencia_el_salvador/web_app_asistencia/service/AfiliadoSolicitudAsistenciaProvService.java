package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoSolicitudAsistencia;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoSolicitudAsistenciaProv;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoSolicitudAsistenciaProvRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoSolicitudAsistenciaProvService {
    private final AfiliadoSolicitudAsistenciaProvRepository repository;

    public AfiliadoSolicitudAsistenciaProvService(
            AfiliadoSolicitudAsistenciaProvRepository repository) {
        this.repository = repository;
    }

    public List<AfiliadoSolicitudAsistenciaProv> buscarPorDUI(String dui){
        return repository.findByDuiAfiliado(dui);
    }

    public List<AfiliadoSolicitudAsistenciaProv> mostrarTodos(){
        return repository.findAll();
    }
}

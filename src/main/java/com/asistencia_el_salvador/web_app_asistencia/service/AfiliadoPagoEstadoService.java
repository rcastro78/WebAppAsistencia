package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPagoEstado;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoPagoEstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoPagoEstadoService {
    @Autowired
    private AfiliadoPagoEstadoRepository afiliadoPagoEstadoRepository;
    public List<AfiliadoPagoEstado> listarTodos(){
        return afiliadoPagoEstadoRepository.findAll();
    }
}

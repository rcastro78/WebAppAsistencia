package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoPagoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.EstadoPagoAfiliadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EstadoPagoAfiliadoService {
    private final EstadoPagoAfiliadoRepository repository;
    EstadoPagoAfiliadoService(EstadoPagoAfiliadoRepository repository) {
        this.repository = repository;
    }
    public List<EstadoPagoAfiliado> getMisAfiliados(String dui) {
        return repository.findByCreatedBy(dui);
    }
}

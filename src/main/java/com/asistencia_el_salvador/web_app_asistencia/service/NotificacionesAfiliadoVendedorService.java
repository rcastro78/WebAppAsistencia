package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.NotificacionesAfiliadoVendedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.NotificacionesAfiliadoVendedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionesAfiliadoVendedorService {
    private final NotificacionesAfiliadoVendedorRepository repository;

    public NotificacionesAfiliadoVendedorService(
            NotificacionesAfiliadoVendedorRepository repository) {
        this.repository = repository;
    }
    public List<NotificacionesAfiliadoVendedor> findByEjecutivoAsignado(String dui) {
        return repository.findByEjecutivoAsignado(dui);
    }
}

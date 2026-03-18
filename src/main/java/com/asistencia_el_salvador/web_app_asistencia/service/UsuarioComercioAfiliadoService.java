package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioAfiliadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioComercioAfiliadoService {
    private UsuarioComercioAfiliadoRepository repository;
    public List<UsuarioComercioAfiliado> mostrarEmpleadosComercio(String nit){
        return repository.findByNit(nit);
    }
}

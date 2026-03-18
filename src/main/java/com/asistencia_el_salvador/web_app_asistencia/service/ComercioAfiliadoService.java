package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.ComercioAfiliadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class ComercioAfiliadoService {
    private final ComercioAfiliadoRepository comercioAfiliadoRepository;

    public ComercioAfiliadoService(ComercioAfiliadoRepository comercioAfiliadoRepository) {
        this.comercioAfiliadoRepository = comercioAfiliadoRepository;
    }

    public List<ComercioAfiliado> listarTodos(){
        return comercioAfiliadoRepository.findAll();
    }

    public ComercioAfiliado getComercioByNIT(String nit){
        return comercioAfiliadoRepository.findByNit(nit);
    }
}

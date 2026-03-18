package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.CategoriaEmpresa;
import com.asistencia_el_salvador.web_app_asistencia.repository.CategoriaEmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaEmpresaService {
    private final CategoriaEmpresaRepository categoriaEmpresaRepository;

    public CategoriaEmpresaService(CategoriaEmpresaRepository categoriaEmpresaRepository) {
        this.categoriaEmpresaRepository = categoriaEmpresaRepository;
    }

    public List<CategoriaEmpresa> listarTodas(){
        return categoriaEmpresaRepository.findAll();
    }
}

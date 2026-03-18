package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.PagoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.PagoAfiliadoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoAfiliadoService{
    private final PagoAfiliadoRepository pagoAfiliadoRepository;

    public PagoAfiliadoService(PagoAfiliadoRepository pagoAfiliadoRepository) {
        this.pagoAfiliadoRepository = pagoAfiliadoRepository;
    }

    public List<PagoAfiliado> listarPagos(String dui){
        return pagoAfiliadoRepository.findByDuiOrderByCreatedAtDesc(dui);
    }

    public List<PagoAfiliado> listarTodos(){
        return pagoAfiliadoRepository.findAll();
    }
}
